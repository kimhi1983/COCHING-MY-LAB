export const validatorPositive = value => {
  if (value >= 0) {
    return true
  }
  return false
}

export const validatorUserId = userId => {
  /* eslint-disable no-useless-escape */
  const regExp = /(?=.*\d)(?=.*[a-zA-Z]).{6,}/
  /* eslint-enable no-useless-escape */
  const validUserId = regExp.test(userId)
  return validUserId
}

export const validatorSimpleUserId = userId => {
  /* eslint-disable no-useless-escape */
  const regExp = /(?=.*\d)(?=.*[a-zA-Z]).{5,}/
  /* eslint-enable no-useless-escape */
  const validUserId = regExp.test(userId)
  return validUserId
}

export const validatorNoWhiteSpace = text => {
  /* eslint-disable no-useless-escape */
  const regExp = /[\s]/g
  /* eslint-enable no-useless-escape */
  const validText = !regExp.test(text)
  return validText
}

export const validatorPassword = password => {
  /* eslint-disable no-useless-escape */
  const regExp = /(?=.*\d)(?=.*[a-z])(?=.*[!@#$%&*()]).{6,}/
  /* eslint-enable no-useless-escape */
  const validPassword = regExp.test(password)
  return validPassword
}

// Scd 가입 비밀번호 : 영문,숫자,특수문자 !@#$%&*()^ 10~12자리
export const validatorPasswordCochingJoin = password => {
  /* eslint-disable no-useless-escape */
  const regExp = /(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%&*()\^]).{10,12}/
  /* eslint-enable no-useless-escape */
  const validPassword = regExp.test(password)
  return validPassword
}

export const validatorCreditCard = creditnum => {
  /* eslint-disable no-useless-escape */
  const cRegExp = /^(?:3[47][0-9]{13})$/
  /* eslint-enable no-useless-escape */
  const validCreditCard = cRegExp.test(creditnum)
  return validCreditCard
}

export const validatorUrlValidator = val => {
  if (val === undefined || val === null || val.length === 0) {
    return true
  }
  /* eslint-disable no-useless-escape */
  const re = /^(http[s]?:\/\/){0,1}(www\.){0,1}[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,5}[\.]{0,1}/
  /* eslint-enable no-useless-escape */
  return re.test(val)
}

export const validatorPhoneValidator = val => {
  if (val === undefined || val === null || val.length === 0) {
    return true
  }
  /* eslint-disable no-useless-escape */
  const re = /^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}/
  /* eslint-enable no-useless-escape */
  return re.test(val)
}

export const validatorTelOnlyNumber = val => {
  /* eslint-disable no-useless-escape */
  const cRegExp = /^[0-9]{2,3}[0-9]{3,4}[0-9]{4}$/
  // const cRegExp = /^[0-9]{9,11}$/
  /* eslint-enable no-useless-escape */

  if (!cRegExp.test(val)) {
    return false;
  }

  const validTelNumber = /^[0-9]{9,11}$/.test(val);
  return validTelNumber;
}

//차량번호 확인
export const validatorVehicleNumber = val => {
  
  //const cRegExp1 = /^[가-힣]{2}[\s]*[0-9]{2}[가~힣]{1}[\s]*[0-9]{4}/gi;
  //const cRegExp2 = /^[0-9]{2,3}[\s]*[가~힣]{1}[\s]*[0-9]{4}/gi;
  const cRegExp1 = /^[가-힣]{2}[0-9]{2}[가-힣]{1}[0-9]{4}$/gi;
  const cRegExp2 = /^[0-9]{2,3}[가-힣]{1}[0-9]{4}$/gi;

  if(cRegExp2.test(val)){
    return true;
  }

  const ret = cRegExp1.test(val);
  
  return ret;
}

// 날짜형식 확인 YYYY-MM-DD
export const validatorYYYYMMDD = val => {
  const regEx = /^\d{4}-\d{2}-\d{2}$/;
    return regEx.test(val);
}