@Injectable()
export class AssessmentService extends TypeOrmCrudService<Assessment> {
constructor(
    @InjectRepository(Assessment) assessmentRepo: Repository<Assessment>,
    private assessmentJobService: AssessmentJobService
  ) {
    super(assessmentRepo);
  }

  // ... other repository related method
  // 使用了大量assessmentJobService的方法

  async linkToJob(id: number, jobId: number): Promise<AssessmentJob> {
    // confirm the assessment exists
    await this.getAssessmentById(id);
    return await this.assessmentJobService.createAssessmentJobReference(id, jobId);
  }

  async updateAssessmentStatus(jobId: number, status: AssessmentStatus): Promise<Assessment> {
    const assessmentJob = await this.getAssessmentJobByJobId(jobId);
    assessmentJob.assessment.status = status;
    return this.repo.save(assessmentJob.assessment);
  }

  async updateAssessmentTradieInfo(
    jobId: number,
    tradieName: string,
    tradieAbn: string
  ): Promise<Assessment> {
    const assessmentJob = await this.getAssessmentJobByJobId(jobId);
    assessmentJob.assessment.tradieName = tradieName;
    assessmentJob.assessment.tradieAbn = tradieAbn;
    return this.repo.save(assessmentJob.assessment);
  }

  async updateAssessmentCost(jobId: number, actualCost: string): Promise<Assessment> {
    const assessmentJob = await this.getAssessmentJobByJobId(jobId);
    assessmentJob.assessment.actualCost = actualCost;
    return this.repo.save(assessmentJob.assessment);
  }

  async updateAssessmentForecastStartDate(
    jobId: number,
    forecastStartDate: Date = new Date()
  ): Promise<Assessment> {
    const assessmentJob = await this.getAssessmentJobByJobId(jobId);
    assessmentJob.assessment.forecastStartDate = forecastStartDate;
    return this.repo.save(assessmentJob.assessment);
  }

  async updateAssessmentJobClosedDate(
    jobId: number,
    jobClosedDate: Date = new Date()
  ): Promise<Assessment> {
    const assessmentJob = await this.getAssessmentJobByJobId(jobId);
    assessmentJob.assessment.jobClosedDate = jobClosedDate;
    return this.repo.save(assessmentJob.assessment);
  }

  getAssessmentJobByJobId(jobId: number): Promise<AssessmentJob> {
    return this.assessmentJobService.getAssessmentJobByJobId(jobId);
  }
}
