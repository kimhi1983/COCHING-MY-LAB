import { extend, localize } from 'vee-validate'
import {
  required as rule_required,
  email as rule_email,
  min as rule_min,
  max as rule_max,
  confirmed as rule_confirmed,
  regex as rule_regex,
  between as rule_between,
  alpha as rule_alpha,
  integer as rule_integer,
  digits as rule_digits,
  alpha_dash as rule_alpha_dash,
  alpha_num as rule_alpha_num,
  length as rule_length,
  is as ruls_is,
} from 'vee-validate/dist/rules'
import ar from 'vee-validate/dist/locale/ar.json'
import en from 'vee-validate/dist/locale/en.json'
import ko from 'vee-validate/dist/locale/ko.json'
import { i18n } from '@/utils/i18n';

// eslint-disable-next-line object-curly-newline
import { validatorPositive, 
  validatorUrlValidator, 
  validatorUserId,
  validatorNiceUserId,
  validatorNoWhiteSpace,
  validatorPassword, 
  validatorCreditCard,
  validatorCreditCard2,
  validatorTelNumber,
  validatorTelOnlyNumber,
  validatorBizNumOnlyNumber,
  validatorCertificationOnlyNumber,
  validatorVehicleNumber,
  validatorEngHanNumber,
  validatorEngHanNumber3,
  validatorPasswordcochingJoin,
  validatorNoSpacesMin10,
  validatorContainsAll,
  validatorPasswordScdJoin,
  validatorUserIdScdJoin,
  validatorDateYYYYMMDD,
  validatorPhone,  
  validatorBankAccount,
} from './validators'

// ////////////////////////////////////////////////////////
// General
// ////////////////////////////////////////////////////////

export const required = extend('required', rule_required)

export const email = extend('email', rule_email)

export const min = extend('min', rule_min)

export const max = extend('max', rule_max)

export const confirmed = extend('confirmed', rule_confirmed)

export const regex = extend('regex', rule_regex)

export const between = extend('between', rule_between)

export const alpha = extend('alpha', rule_alpha)

export const integer = extend('integer', rule_integer)

export const digits = extend('digits', rule_digits)

export const alphaDash = extend('alpha-dash', rule_alpha_dash)

export const alphaNum = extend('alpha-num', rule_alpha_num)

export const length = extend('length', rule_length)

export const is = extend('is', ruls_is)

export const positive = extend('positive', {
  validate: validatorPositive,
  message: 'Please enter positive number!',
})

export const credit = extend('credit-card', {
  validate: validatorCreditCard,
  message: 'It is not valid credit card!',
})

export const cochingValidation = vm => {
  extend('credit2', {
    validate: value => {
      return validatorCreditCard2(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 숫자만 14~16자로 입력해 주십시오.';
    },
  });

  extend('userId', {
    validate: value => { 
      return validatorUserId(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 영문자, 숫자를 포함하여야 합니다.';
    }
  });

  extend('no-whitespace', {
    validate: value => {
      return validatorNoWhiteSpace(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 공백이 포함될 수 없습니다.';
    }
  });

  extend('no-niceUserId', {
    validate: value => {
      return validatorNiceUserId(value) ? true : i18n.t('') || '사용할 수 없는 아이디 입니다.';
    }
  });

  extend('password', {
    validate: value => {
      return validatorPassword(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 영문자, 소문자, 숫자, 특수문자 !@#$%&*()^ 가 반드시 하나 이상 들어가야 합니다.';
    }
  });

  extend('telNumber', {
    validate: value => {
      return validatorTelNumber(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 010-0000-0000 형태로 입력해 주십시오.';
    }
  });

  extend('telOnlyNumber', {
    validate: value => {
      return validatorTelOnlyNumber(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 01012345678 형태로 숫자로만 입력해 주십시오.';
    }
  });

  extend('bizNumOnlyNumber', {
    validate: value => {
      return validatorBizNumOnlyNumber(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 123567890 형태로 숫자로만 입력해 주십시오.';
    }
  });

  extend('certificationNumber', {
    validate: value => {
      return validatorCertificationOnlyNumber(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 6자리 숫자로 입력해 주십시오.';
    }
  });

  extend('vehicleNumber', {
    validate: value => {
      return validatorVehicleNumber(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 공백없이 111가1111 형태로 입력해 주십시오.';
    }
  });

  extend('engHanNumber', {
    validate: value => {
      return validatorEngHanNumber(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 공백없이 영문,한글,숫자만 입력해 주십시오.';
    }
  });

  extend('engHanNumber3', {
    validate: value => {
      return validatorEngHanNumber3(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 공백없이 영문,한글,숫자만 3자이상 입력해 주십시오.';
    }
  });

  extend('passwordcochingJoin', {
    validate: value => {
      return validatorPasswordcochingJoin(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 영문,숫자,특수문자 !@#$%&*()^ 포함하여 10~12자리로 입력해 주십시오.';
    },
  });

  extend('passwordNoSpacesMin10', {
    validate: value => {
      return validatorNoSpacesMin10(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 공백 제외하고 10이상으로 입력해 주십시오.';
    },
  });

  extend('passwordContainsAll', {
    validate: value => {
      return validatorContainsAll(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 영문,숫자,특수문자 !@#$%&*()^ 포함하여 입력해 주십시오.';
    },
  });
  
  extend('dateYYYYMMDD', {
    validate: value => {
      return validatorDateYYYYMMDD(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 19990925 형태로 8자리 숫자로 입력해 주십시오.';
    }
  });

  extend('phone', {
    validate: value => {
      return validatorPhone(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}는 010-1234-5678 형태로 입력해 주십시오.';
    }
  });

  extend('bankAccount', {
    validate: value => {
      return validatorBankAccount(value) ? true : i18n.t('', { _field_: `{_field_}` }) || '{_field_}를 숫자만 8자이상 입력해 주십시오.';
    },
  });

};

// export const credit2 = extend('credit2', {
//   validate: validatorCreditCard2,
//   message: i18n.t('validation.credit2', { _field_: `{_field_}` }) || '{_field_}는 숫자만 14~16자로 입력해 주십시오.',
// })

// export const userId = extend('userId', {
//   validate: validatorUserId,
//   message: i18n.t('validation.userId', { _field_: `{_field_}` }) || '{_field_}는 영문자, 숫자를 포함하여야 합니다.',
// });

// export const noWhiteSpace = extend('no-whitespace', {
//   validate: validatorNoWhiteSpace,
//   message: i18n.t('validation.noWhiteSpace', { _field_: `{_field_}` }) || '{_field_}는 공백이 포함될 수 없습니다.',
// });

// export const noNiceUserId = extend('no-niceUserId', {
//   validate: validatorNiceUserId,
//   message: i18n.t('validation.noNiceUserId') || '사용할 수 없는 아이디 입니다.',
// });

// export const password = extend('password', {
//   validate: validatorPassword,
//   message: i18n.t('validation.password', { _field_: `{_field_}` }) || '{_field_}는 영문자, 소문자, 숫자, 특수문자 !@#$%&*()^ 가 반드시 하나 이상 들어가야 합니다.',
// });

// export const telNumber = extend('telNumber', {
//   validate: validatorTelNumber,
//   message: i18n.t('validation.telNumber', { _field_: `{_field_}` }) || '{_field_}는 010-0000-0000 형태로 입력해 주십시오.',
// });

// export const telOnlyNumber = extend('telOnlyNumber', {
//   validate: validatorTelOnlyNumber,
//   message: i18n.t('validation.telOnlyNumber', { _field_: `{_field_}` }) || '{_field_}는 01012345678 형태로 숫자로만 입력해 주십시오.',
// });

// export const bizNumOnlyNumber = extend('bizNumOnlyNumber', {
//   validate: validatorBizNumOnlyNumber,
//   message: i18n.t('validation.bizNumOnlyNumber', { _field_: `{_field_}` }) || '{_field_}는 123567890 형태로 숫자로만 입력해 주십시오.',
// });

// export const certificationNumber = extend('certificationNumber', {
//   validate: validatorCertificationOnlyNumber,
//   message: i18n.t('validation.certificationNumber', { _field_: `{_field_}` }) || '{_field_}는 6자리 숫자로 입력해 주십시오.',
// });

// export const vehicleNumber = extend('vehicleNumber', {
//   validate: validatorVehicleNumber,
//   message: i18n.t('validation.vehicleNumber', { _field_: `{_field_}` }) || '{_field_}는 공백없이 111가1111 형태로 입력해 주십시오.',
// });

// export const engHanNumber = extend('engHanNumber', {
//   validate: validatorEngHanNumber,
//   message: i18n.t('validation.engHanNumber', { _field_: `{_field_}` }) || '{_field_}는 공백없이 영문,한글,숫자만 입력해 주십시오.',
// });

export const url = extend('url', {
  validate: validatorUrlValidator,
  message: 'URL is invalid',
})

// export const passwordcochingJoin = extend('passwordcochingJoin', {
//   validate: validatorPasswordcochingJoin,
//   message: i18n.t('validation.passwordcochingJoin', { _field_: `{_field_}` }) || '{_field_}는 영문,숫자,특수문자 !@#$%&*()^ 포함하여 10~12자리로 입력해 주십시오.',
// });

// export const passwordScdJoin = extend('passwordScdJoin', {
//   validate: validatorPasswordScdJoin,
//   message: i18n.t('validation.passwordScdJoin', { _field_: `{_field_}` }) || '{_field_}는 영문,숫자,특수문자 !@#$%&*()^ 포함 되어야 합니다.',
// });

// export const userIdScdJoin = extend('userIdScdJoin', {
//   validate: validatorUserIdScdJoin,
//   message: i18n.t('validation.userIdScdJoin', { _field_: `{_field_}` }) || '{_field_}는 영문,숫자를 포함하여 6~9자리로 입력해 주십시오.',
// });

// export const dateYYYYMMDD = extend('dateYYYYMMDD', {
//   validate: validatorDateYYYYMMDD,
//   message: i18n.t('validation.dateYYYYMMDD', { _field_: `{_field_}` }) || '{_field_}는 19990925 형태로 8자리 숫자로 입력해 주십시오.',
// });

// export const phone = extend('phone', {
//   validate: validatorPhone,
//   message: i18n.t('validation.phone', { _field_: `{_field_}` }) || '{_field_}는 010-1234-5678 형태로 입력해 주십시오.',
// });

// export const bankAccount = extend('bankAccount', {
//   validate: validatorBankAccount,
//   message: i18n.t('validation.bankAccount', { _field_: `{_field_}` }) || '{_field_}를 숫자만 8자이상 입력해 주십시오.',
// });

// Install English and Arabic localizations.
localize({
  ko: {
    messages: ko.messages,
    names: {
      email: 'Email',
      password: 'Password',
    },
    fields: {
      password: {
        min: '{_field_}는 최소 6자리 이상 설정해 주십시오.',
      },
    },
  },
  en: {
    messages: en.messages,
    names: {
      email: 'Email',
      password: 'Password',
    },
    fields: {
      password: {
        min: '{_field_} is too short, you want to get hacked?',
      },
    },
  },
  ar: {
    messages: ar.messages,
    names: {
      email: 'البريد الإلكتروني',
      password: 'كلمة السر',
    },
    fields: {
      password: {
        min: 'كلمة السر قصيرة جداً سيتم اختراقك',
      },
    },
  },  
})
// ////////////////////////////////////////////////////////
// NOTE:
// Quasar validation for reference only
// Remove this note once development is finished and make sure to
// to convert all of them in veevalidate version
// ////////////////////////////////////////////////////////

// export const required = (val) => {
//   return (val && val.length > 0) || '*Field is required'
// }

// export const required_obj = (obj) => {
//   if (obj === null || obj === undefined) return '*Field is required'
//   return (Object.entries(obj).length > 0 && obj.constructor === Object) || '*Field is required'
// }

// export const no_blank_spaces_arr = (arr) => {
//   return arr.every(val => (val.trim() && val.trim().length > 0)) || 'Blank Spaces are not allowed'
// }

// export const url = val => {
//   // If blank return
//   if (val === undefined || val === null || val.length === 0) return true

//   // Used
//   // https://stackoverflow.com/questions/4314741/url-regex-validation

//   // Other
//   // https://stackoverflow.com/questions/5717093/check-if-a-javascript-string-is-a-url
//   // https://www.w3resource.com/javascript-exercises/javascript-regexp-exercise-9.php
//   // https://www.geeksforgeeks.org/how-to-validate-url-using-regular-expression-in-javascript/

//   /* eslint-disable no-useless-escape */
//   const re = /^(http[s]?:\/\/){0,1}(www\.){0,1}[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,5}[\.]{0,1}/
//   /* eslint-enable no-useless-escape */
//   return re.test(val) || 'URL is invalid'
// }

// export const date = val => {

//   // If blank return
//   if (val === undefined || val === null || val.length === 0) return true

//   // https://github.com/quasarframework/quasar/blob/dev/ui/src/utils/patterns.js
//   return /^-?[\d]+\/[0-1]\d\/[0-3]\d$/.test(val) || 'Date is invalid'
// }

// export const max = (val, max) => {
//   // If blank return
//   if (val === undefined || val === null) return true
//   return val.length <= max || `More than ${max} characters are not allowed`
// }

// export const max_arr = (val, max) => {
//   return val.length <= max || `More than ${max} values are not allowed`
// }

// export const min = (val, min) => {

//   // If blank return
//   if (val === undefined || val === null || val.length === 0) return true

//   return val.length >= min || `Minimum ${min} characters are required`
// }

// export const num_range = (val, min, max) => {

//   // If blank return
//   if (val === undefined || val === null || val.length === 0) return true

//   const msg = 'Value is invalid'
//   if (min === null) return val <= max || msg
//   else if (max === null) return val >= min || msg
//   else return (val >= min && val <= max) || msg
// }
