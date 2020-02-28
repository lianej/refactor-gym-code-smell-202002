    /**
     *
     * NOTE: $sms_daemon_task_ids must have been locked before calling this function.
     * Daemon SMS Daemon send function
     *
     * @param array  $sms_daemon_task_ids - SMS daemon task ids
     * @param string $sms_error_log_file
     * @param bool   $debug
     */
    public function sendInBatches($sms_daemon_task_ids, $sms_error_log_file = '', $debug = false)
    {
        if (!is_array($sms_daemon_task_ids) || empty($sms_daemon_task_ids)) {
            return;
        }

        /**
         * Step 1: collect the ready to go sms message details:
         * we need to create 2 sets of task ids: one set is for 1way sms, the other set is for 2way sms.
         */
        echo count($sms_daemon_task_ids) . ' sms daemon task ids.' . PHP_EOL;

        $sms_messages = array();    //the index will be the daemon task id.
        $daemon_tasks = array();    //the index will be the daemon task id.

        $unique_sms_message_ids = array(); //we are not going to send duplicate sms messages.
        $valid_daemon_task_ids = array(); //Only these daemon task ids will have the process id.

        //the following 3 vars holds 3 different ready-to-go sms messages:
        $one_way_sms_ppl = array();
        $one_way_sms_other = array();
        $two_way_sms = array();

        $sms = new VTK_Service_SMS();
        $sms->debug = $debug;

        $featureService = new VTK_Service_Feature();
        //looping through all daemon task ids:
        foreach ($sms_daemon_task_ids as $sms_daemon_task_id) {
            /** @var VTK_Model_Daemon_SMS $sMSDaemonTask */
            $sMSDaemonTask = new VTK_Model_Daemon_SMS();
            $sMSDaemonTask = $this->dao()->readObject(
                $sMSDaemonTask,
                '`sms_daemon_task_id` = ?',
                array($sms_daemon_task_id)
            );

            //Process this task only if it still exists:
            if (!empty($sMSDaemonTask)) {
                //VERY IMPORTANT: initialize the $sms_details
                $sms_details = null;

                $daemon_tasks[$sms_daemon_task_id] = $sMSDaemonTask;

                //get SMS message id
                $sMSMessageId = $sMSDaemonTask->getExternalId();

                //check to see whether the sms message id is valid
                if ($sMSMessageId > 0) {
                    $sMSModel = new VTK_Model_SMS();

                    /** @var VTK_Dao_SMS $sMSDao */
                    $sMSDao = $this->dao()->getDao('VTK_Dao_SMS');
                    $sMSDao->readObject($sMSModel, '`sms_message_id` = ?', array($sMSMessageId));

                    // if the number is in the ignored list, setting the status to sent
                    if (in_array($sMSModel->getTo(), VTK_Model_SMS::$IGNORED_NUMBERS)) {
                        $sMSModel->setStatus('Sent');
                        $this->dao()->saveObject($sMSModel);
                        $smsDao = new VTK_Dao_SMS();
                        $smsDao->updateJobSMSStatus($sMSModel);
                    }

                    #GROW-788 [comms]Suppress comms for fixed price model - backend
                    if ($featureService->suppressStandardConsumerCommsForSMS($sMSModel)) {
                        //please be in mind, this also update original $sMSModel
                        $sMSModelClone = $sMSModel->cloneMutable(true);
                        $sMSModelClone->setStatus('Ignore');
                        $sMSModelClone->setIgnoredReason('Suppress SMS('.$sMSModel->getType().') for thread #'.$sMSModel->getThreadId());
                        $this->dao()->saveObject($sMSModelClone);
                    }

                    //4 types of SMS: New, Scheduled, Ignored, Sent.
                    switch ($sMSModel->getStatus()) {
                        case 'New':
                            //send it through SMS service
                            if ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_ENQUIRY) {
                                //get details only and do not send it straight away:

                                $sms_details = $sms->getEnquirySMSDetailsById($sMSMessageId);
                            } elseif (in_array($sMSModel->getType(), array(VTK_Model_SMS::SMS_TYPE_CONSUMER_JOB_NOTIFICATION, VTK_Model_SMS::SMS_TYPE_CONSUMER_JOB_CLAIM_NOTIFICATION))) {
                                //get details only and do not send it straight away:
                                $sms_details = $sms->getConsumerJobNotificationSMSDetailsById($sMSMessageId);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_JOB_INVITATION_REMINDER_NOTIFICATION) {
                                //get details only and do not send it straight away:
                                $sms_details = $sms->sendSMSJobInvitationReminder($sMSMessageId, true, VTK_Model_SMS::SMS_TYPE_JOB_INVITATION_REMINDER_NOTIFICATION);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_JOB_INVITATION_OUT_OF_TARGET_NOTIFICATION) {
                                //get details only and do not send it straight away:
                                $sms_details = $sms->sendSMSJobInvitationReminder($sMSMessageId, true, VTK_Model_SMS::SMS_TYPE_JOB_INVITATION_OUT_OF_TARGET_NOTIFICATION);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_BACKEDUP_CLAIM_CONSUMER_NOTIFICATION) {
                                //get details only and do not send it straight away:
                                $sms_details = $sms->sendScheduledSMSJobInvitationReminder($sMSMessageId, true, VTK_Model_SMS::SMS_TYPE_BACKEDUP_CLAIM_CONSUMER_NOTIFICATION);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_JOB_INVITATION_DIRECTORY_VLP_NOTIFICATION) {
                                //get details only and do not send it straight away:
                                $sms_details = $sms->sendSMSJobInvitationReminder($sMSMessageId, true, VTK_Model_SMS::SMS_TYPE_JOB_INVITATION_DIRECTORY_VLP_NOTIFICATION);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_VALUE_PRODUCT_SURFACE_NOTIFICATION) {
                                $sms_details = $sms->sendVPSurfaceNotificationSMS($sMSMessageId);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_CONCIERGE_FOLLOWUP ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_CONCIERGE_FOLLOWUP_NOT_INTERESTED_REASON ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_CONSUMER_JOB_OUTCOME_REMINDER
                            ) {
                                $sms_details = $sms->getJobConciergeFollowupSMSDetailsBySmsId($sMSMessageId);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_ASK_CONSUMER_WHETHER_JOB_COMPLETED) {
                                $sms_details = $sms->getJobCompletionConfirmSMSDetailsBySmsId($sMSMessageId);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_ASK_CONSUMER_WHETHER_JOB_COMPLETED_FOLLOW_UP) {
                                $sms_details = $sms->getJobCompletionConfirmFollowUpSMSDetailsBySmsId($sMSMessageId);

                                // BUN-686 Don't allow consumer to mark bunnings fixed priced job as completed if the voucher has already been refunded
                                $jobAssignmentId = $sMSModel->getThreadId();
                                $jobAssignment = $this->getJobAssignmentDao()->getJobAssignmentById($jobAssignmentId);
                                $jobId = $jobAssignment->getJobId();
                                if (empty($jobAssignment) || $jobId <= 0) {
                                    $sms_details->setStatus('Ignored');
                                    break;
                                }

                                $notAllowToMarkJobComplete = $this->getUtilityBunnings()->notAllowConsumerToMarkBunningsFixedPriceJobAsCompleted($jobId);
                                if ($notAllowToMarkJobComplete) {
                                    $sms_details->setStatus('Ignored');
                                    break;
                                }

                                //here we need to mark  the job complete  by consumer
                                $service_job_assignment_complete = new VTK_Service_Job_Assignment_Complete;
                                $service_job_assignment_complete->updateConsumerComplete(
                                    $sMSModel->getThreadId(),
                                    VTK_Model_Job_Assignment_Complete::STATUS_COMPLETED,
                                    VTK_Model_Job_Assignment_Complete_History::ACTION_COMPLETED,
                                    0,
                                    VTK_Model_Job_Assignment_Complete_History::FEATURE_FEEDBACK,
                                    VTK_Model_Job_Assignment_Complete_History::CHANNEL_SMS,
                                    VTK_Model_Job_Assignment_Complete_History::REFERRER_UNKNOWN
                                );
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_REENGAGEMENT_REMINDER) {
                                $sms_details = $sms->sendReenagmentReminderSMS($sMSMessageId);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_AUTOMATICALLY_ENABLED_SHORT_SMS_NOTIFICATION ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_HONEYPOT_NOTIFICATION ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_SWITCHED_TO_INVITATION_DIGEST ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_APP_PROMOTION ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_EXTRA_SMS_DELAY_APP_CLIENT_NOTIFICATION ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_AUTOMATICALLY_DISABLED_SMS ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_TRIAL_WALL ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_FREEMIUM_WALL ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_PAY_WALL ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_INVOICE_REMINDER ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_PENDING_PAYMENT_CONSUMER ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_VOUCHER_REFUND_CONSUMER_NOTIFICATION ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_VOUCHER_REFUND_TRADIE_NOTIFICATION ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_CONSUMER_FEEDBACK_NOTIFICATION ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_CALL_SCHEDULED_NOW ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_PASSWORDLESS_LOGIN_CODE ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_DOWNLOAD_APP_AFTER_SALE_COMPLETED
                            ) {
                                $sms_details = $sms->markSMSSent($sMSMessageId);
                            } else {
                                //NPD-688
                                //Update SMS Invitation Daemon to ignore tasks where job is closed
                                // only when sms type is SMS_TYPE_INVITATION
                                if ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_INVITATION) {
                                    if ($this->ignoreTasksWhenJobIsClosed($sMSDaemonTask, $sMSModel)) {
                                        break;
                                    }
                                }
                                $sms_details = $sms->getJobInvitationSMSDetailsBySmsId($sMSMessageId);
                            }

                            break;

                        case 'Ignored':
                        case 'Sent': // Intentional fall through
                            $sms_details = null;

                            //delete sms daemon task
                            $this->dao()->deleteObject($sMSDaemonTask);
                            break;

                        break;

                        case 'Scheduled':
                            //send it through SMS service
                            if ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_ENQUIRY) {
                                //get details only and do not send it straight away:
                                $sms_details = $sms->getScheduledEnquirySMSDetailsById($sMSMessageId);
                            } elseif (in_array($sMSModel->getType(), array(VTK_Model_SMS::SMS_TYPE_CONSUMER_JOB_NOTIFICATION, VTK_Model_SMS::SMS_TYPE_CONSUMER_JOB_CLAIM_NOTIFICATION))) {
                                //get details only and do not send it straight away:
                                $sms_details = $sms->getConsumerJobNotificationSMSDetailsById($sMSMessageId);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_JOB_INVITATION_REMINDER_NOTIFICATION) {
                                //get details only and do not send it straight away:
                                $sms_details = $sms->sendScheduledSMSJobInvitationReminder($sMSMessageId, true, VTK_Model_SMS::SMS_TYPE_JOB_INVITATION_REMINDER_NOTIFICATION);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_JOB_INVITATION_OUT_OF_TARGET_NOTIFICATION) {
                                //get details only and do not send it straight away:
                                $sms_details = $sms->sendScheduledSMSJobInvitationReminder($sMSMessageId, true, VTK_Model_SMS::SMS_TYPE_JOB_INVITATION_OUT_OF_TARGET_NOTIFICATION);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_BACKEDUP_CLAIM_CONSUMER_NOTIFICATION) {
                                //get details only and do not send it straight away:
                                $sms_details = $sms->sendScheduledSMSJobInvitationReminder($sMSMessageId, true, VTK_Model_SMS::SMS_TYPE_BACKEDUP_CLAIM_CONSUMER_NOTIFICATION);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_JOB_INVITATION_DIRECTORY_VLP_NOTIFICATION) {
                                //get details only and do not send it straight away:
                                $sms_details = $sms->sendScheduledSMSJobInvitationReminder($sMSMessageId, true, VTK_Model_SMS::SMS_TYPE_JOB_INVITATION_DIRECTORY_VLP_NOTIFICATION);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_VALUE_PRODUCT_SURFACE_NOTIFICATION) {
                                $sms_details = $sms->sendVPSurfaceNotificationSMS($sMSMessageId);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_REENGAGEMENT_REMINDER) {
                                $sms_details = $sms->sendReenagmentReminderSMS($sMSMessageId);
                            } elseif ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_EXTRA_SMS_DELAY_APP_CLIENT_NOTIFICATION ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_AUTOMATICALLY_DISABLED_SMS ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_TRIAL_WALL ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_FREEMIUM_WALL ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_PAY_WALL ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_INVOICE_REMINDER ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_AUTOMATICALLY_ENABLED_SHORT_SMS_NOTIFICATION ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_VOUCHER_REFUND_CONSUMER_NOTIFICATION ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_VOUCHER_REFUND_TRADIE_NOTIFICATION ||
                            $sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_CONSUMER_FEEDBACK_NOTIFICATION
                            ) {
                                $sms_details = $sms->markSMSSent($sMSMessageId);
                            } else {
                                //NPD-688
                                //Update SMS Invitation Daemon to ignore tasks where job is closed
                                // only when sms type is SMS_TYPE_INVITATION
                                if ($sMSModel->getType() == VTK_Model_SMS::SMS_TYPE_INVITATION) {
                                    if ($this->ignoreTasksWhenJobIsClosed($sMSDaemonTask, $sMSModel)) {
                                        break;
                                    }
                                }
                                $sms_details = $sms->getJobInvitationSMSDetailsBySmsId($sMSMessageId);
                            }

                            break;
                        default:
                            //invalid status or this sms message doesn't exist at all.
                    }

                    /**
                     * ###########checking $sms_details##########
                     *
                     * Here we need to check the $sms_details and push them into 3 different buckets:
                     * the returned status can only have 3 values: 'Sent', 'Scheudled', 'Ignored', which means to be sent or to be scheduled.
                     */

                    if ($sms_details instanceof VTK_Model_SMS && !isset($unique_sms_message_ids[$sms_details->getID()])) {
                        //this is very very important, as the duplicate messages will be rejected by sms central:
                        $unique_sms_message_ids[$sms_details->getID()] = $sms_details->getID();

                        //Remember the daemon task id
                        $sms_details->setDaemonTaskId($sms_daemon_task_id)
                            ->setDaemonObject($sMSDaemonTask);

                        //status == 'Sent' means to be sent, because we injected the existing sms functions and didn't change the status.
                        //Note: this status is NOT saved yet.
                        if ($sms_details->getStatus() == 'Sent') {
                            $sms_messages[$sms_daemon_task_id] = $sms_details;
                            $valid_daemon_task_ids[] = $sms_daemon_task_id;

                            //here we need to push the sms into 3 different buckets:
                            if ($sms_details->getApi() == 0) {
                                //one way sms:
                                if ($sms_details->getEcpl() > 0) {
                                    //one way PPL sms:
                                    $one_way_sms_ppl[] = $sms_details;
                                } else {
                                    //one way non-PPL sms:
                                    $one_way_sms_other[] = $sms_details;
                                }
                            } else {
                                //two way sms:
                                $two_way_sms[] = $sms_details;
                            }
                        } else {
                            //Note: sms status & ignored reason already saved into database by now.

                            //update current sms daemon task if its scheduled datetime is not empty
                            if ($sms_details->getStatus() == 'Scheduled' && $sms_details->getScheduleDatetime() > 0) {
                                $today = Zend_Date::now();
                                $sMSDaemonTask->setIsLocked(0)
                                    ->setScheduledAt($sms_details->getScheduleDatetime())
                                    ->setModifiedBy(0)
                                    ->setModifiedByDate($today->getTimestamp());
                                $this->dao()->saveObject($sMSDaemonTask);
                            } else {
                                //SMS message has been ignored
                                //delete sms daemon task
                                //echo "Ignored by sms functions for some reason, deleting this daemon task...\n";
                                $this->dao()->deleteObject($sMSDaemonTask);
                            }
                        }
                    } else {
                        //invalid sms record or duplicate sms messages.
                        //print_r($sMSModel);
                        //echo "Invalid sms or duplicate sms , deleting...\n";
                        //exit;
                        $this->dao()->deleteObject($sMSDaemonTask);
                    }
                    ###########end of checking $sms_details##########
                } //end of processing one sms message
                //otherwise delte it from sms_daemon_tasks table, because this is an invalid task:
                else {
                    //echo "Invalid task as the sms message id is invalid, deleting this task...\n";
                    $this->dao()->deleteObject($sMSDaemonTask);
                }
            } //end of processing one daemon task
        } //end of the looping all task ids.

        //step 2: sending sms messages in batches:
        //save process id for these tasks:
        if (!empty($valid_daemon_task_ids)) {
            $this->dao()->setProcessIdByTaskIds($valid_daemon_task_ids);
        }

        // Step 3: send sms , archive & delete the daemon tasks.

        $sms->sendSMSThroughSmsCentralInBatches($one_way_sms_other, 0, 0, 100, $sms_error_log_file);
        $sms->sendSMSThroughSmsCentralInBatches($two_way_sms, 1, 0, 100, $sms_error_log_file);
        $sms->sendSMSThroughSmsCentralInBatches($one_way_sms_ppl, 0, 1, 100, $sms_error_log_file);

        echo count($one_way_sms_ppl) . ' one way ppl sms' . PHP_EOL;
        echo count($one_way_sms_other) . ' one way other sms ' . PHP_EOL;
        echo count($two_way_sms) . ' two way sms ' . PHP_EOL;
        echo count($sms_messages) . ' messages  to be sent.' . PHP_EOL;
    }