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

export const validatorCochingUserId = userId => {
  /* eslint-disable no-useless-escape */
  const regExp = /(?=.*[a-zA-Z]).{6,}/
  /* eslint-enable no-useless-escape */
  const validUserId = regExp.test(userId)
  return validUserId
}

export const validatorNiceUserId = niceUserId => {
  /* eslint-disable no-useless-escape */
  const regExp = /^so[\d]*/g
  /* eslint-enable no-useless-escape */
  const validNiceUserId = !regExp.test(niceUserId)
  return validNiceUserId
}

export const validatorNoWhiteSpace = text  => {
  /* eslint-disable no-useless-escape */
  const regExp = /[\s]/g
  /* eslint-enable no-useless-escape */
  const validText = !regExp.test(text)
  return validText
}

export const validatorPassword = password => {
  /* eslint-disable no-useless-escape */
  const regExp = /(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%&*()\^]).{6,}/
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

export const validatorCreditCard2 = creditnum => {
  /* eslint-disable no-useless-escape */
  const cRegExp = /^[0-9]{14,16}$/
  /* eslint-enable no-useless-escape */
  const validCreditCard = cRegExp.test(creditnum)
  return validCreditCard
}

export const validatorBankAccount = bankAccounNum => {
  /* eslint-disable no-useless-escape */
  const cRegExp = /^[0-9]{8,}$/
  /* eslint-enable no-useless-escape */
  const validBankAccount = cRegExp.test(bankAccounNum)
  return validBankAccount
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

export const validatorTelNumber = val => {
  /* eslint-disable no-useless-escape */
  const cRegExp = /^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}/
   /* eslint-enable no-useless-escape */
   const validTelNumber = cRegExp.test(val)
   return validTelNumber;  
}


export const validatorTelOnlyNumber = val => {
  /* eslint-disable no-useless-escape */
  const cRegExp = /^[0-9]{2,3}[0-9]{3,4}[0-9]{4}$/
  // const cRegExp = /^[0-9]{9,11}$/
   /* eslint-enable no-useless-escape */

  if(!cRegExp.test(val)){
    return false;
  }
  
  const validTelNumber = /^[0-9]{9,11}$/.test(val);
  return validTelNumber;  
}

export const validatorBizNumOnlyNumber = val => {
  /* eslint-disable no-useless-escape */
  const cRegExp = /^[0-9]{10}$/gi
   /* eslint-enable no-useless-escape */
   const validTelNumber = cRegExp.test(val)
   return validTelNumber;  
}

export const validatorCertificationOnlyNumber = val => {
  /* eslint-disable no-useless-escape */
  const cRegExp = /^[0-9]{6}$/gi
   /* eslint-enable no-useless-escape */
   const validCNumber = cRegExp.test(val)
   return validCNumber;  
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

// 영문, 숫자, 한글 1자 이상
export const validatorEngHanNumber = val =>{
  const cRegExp = /^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|]+$/gi;

  const validInput = cRegExp.test(val)
  return validInput; 
}

// 영문한글숫자 조합 3글자 이상, 숫자만 3글자는 불허용
export const validatorEngHanNumber3 = val =>{
  const cRegExp = /^(?!^\d{3,}$)(?=.*[a-zA-Zㄱ-ㅎ가-힣])[a-zA-Zㄱ-ㅎ가-힣0-9]{3,}$/;

  const validInput = cRegExp.test(val)
  return validInput; 
}

// 가입 비밀번호 : 영문,숫자,특수문자 !@#$%&*()^ 10~12자리
export const validatorPasswordcochingJoin = password =>{
  /* eslint-disable no-useless-escape */
  const regExp = /(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%&*()\^]).{10,12}/
  /* eslint-enable no-useless-escape */
  const validPassword = regExp.test(password)
  return validPassword
}

//가입 비밀번호 : 최소 10자리 이상
export const validatorNoSpacesMin10 = password => {
  /* eslint-disable no-useless-escape */
  const regExp = /^(?=\S+$).{10,}$/
  /* eslint-enable no-useless-escape */
  return regExp.test(password)
}

//가입 비밀번호 : 영문, 숫자, 특수문자 !@#$%&*()^
export const validatorContainsAll = password => {
  /* eslint-disable no-useless-escape */
  const regExp = /^(?=.*[A-Za-z])(?=.*\d|.*[!@#$%&*()\^])|(?=.*\d)(?=.*[!@#$%&*()\^])$/;
  /* eslint-enable no-useless-escape */
  return regExp.test(password)
}

// 날짜 yyyyMMdd
export const validatorDateYYYYMMDD = val => {
  /* eslint-disable no-useless-escape */
  const regExp = /^\d{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$/
  /* eslint-enable no-useless-escape */
  const validUserId = regExp.test(val)
  return validUserId
}

// 휴대폰 번호
export const validatorPhone = val => {
  /* eslint-disable no-useless-escape */
  const regExp = /^01([0|1|6|7|8|9]?)-?([0-9]{3,4})-?([0-9]{4})$/
  /* eslint-enable no-useless-escape */
  const validUserId = regExp.test(val)
  return validUserId
}