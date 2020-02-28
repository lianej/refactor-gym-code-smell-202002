
// 表单提交的处理函数
const onSubmit = formValue => {
  const { email } = formValue;
  const pattern = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
  if (pattern.test(email)) {
    // submit form
  } else {
    // show error message
  }
};


// 一个专门抽取做字段校验的Util文件其中的邮件名校验方法

export const validEmailByPattern = email => {
  const pattern = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
  return pattern.test(email);
};
