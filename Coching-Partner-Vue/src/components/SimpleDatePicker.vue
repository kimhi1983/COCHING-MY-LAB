<template>
  <div class="datepicker-input">
    <input type="text" name="dateBox" ref="inputDatePicker" 
      :readonly="readonly"
      :disabled="disabled"
      :placeholder="placeholder"
    />
  </div>
</template>

<script>
import moment from 'moment';

const DEF_DATEPICKER_OPTION = {
  dateFormat: "y년 m월 d일 DD",
  // showOtherMonths: true,
  showMonthAfterYear: true,
  yearSuffix: "년",
  dayNames: ['(일)', '(월)', '(화)', '(수)', '(목)', '(금)', '(토)'],
  dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
  monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'], 
};

const DEF_DATEPICKER_OPTION_EN = {
  dateFormat: "MMM d, yy",
  showMonthAfterYear: true,
  yearSuffix: "",
  dayNames: ['(S)', '(M)', '(T)', '(W)', '(T)', '(F)', '(S)'],
  dayNamesMin: ['S', 'M', 'T', 'W', 'T', 'F', 'S'],
  monthNames: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
};

export default {
  props: {
    value: {
        type: String,
        default: ""
    },
    options: {
        type: [Object],
        default: () => []
    },
    disabled: {
        type: Boolean,
        default: false
    },
    placeholder: {
      type: String,
      default: ""
    },
    readonly: {
        type: Boolean,
        default: true
    },
    dimOverlayClass :{
      type: [String],
      default: "datepicker-dim-overlay"
    },
    onValueReturn: {
        type: Object,
        default: () => ({})
    }    
  },
  watch : {
    value(newVal, oldVal) {
      this.setValue();
    },    
    '$i18n.locale' : function(){
        const _vm = this;
        _vm.init();
      },
  },
  computed: {
    datePickerRef(){
      return this.$refs.inputDatePicker;
    }
  },
  data() {
		return {
      localOption : {
        ...DEF_DATEPICKER_OPTION
      },
      localVal : ""
    }
  },
  mounted() {
    const _vm = this;
    _vm.init();
  },
  beforeDestroy() {
		const _vm = this;

    $(_vm.datePickerRef).datepicker('hide').datepicker('destroy');
    _vm.removeDim();
	},
  methods: {
    setParam(){
      const _vm = this;
      
      _vm.localVal = _vm.value || moment().format('YYYY-MM-DD');      
    },

    setMinDate(minDate){
      const _vm = this;

      console.debug(minDate);
      $(_vm.datePickerRef).datepicker("option", "minDate", minDate);
    },

    setMaxDate(maxDate){
      const _vm = this;

      console.debug(maxDate);
      $(_vm.datePickerRef).datepicker("option", "maxDate", maxDate);
    },

    init(){
      const _vm = this;

      $(_vm.datePickerRef).datepicker('hide').datepicker('destroy');
      _vm.removeDim();

      //DatePicker 초기화
      _vm.setParam();
      const initStrDate = _vm.localVal;
      const initDate = moment(initStrDate).toDate(); //Date Type

      let finalCalendar = null;
      if(_vm.$i18n.locale === 'ko') {
        finalCalendar = DEF_DATEPICKER_OPTION;
      } else if(_vm.$i18n.locale === 'en') {
        finalCalendar = DEF_DATEPICKER_OPTION_EN;
      }

      _vm.localOption = {
        ...finalCalendar,
        ..._vm.options,

        beforeShow: function (input, inst) {
          $(_vm.datePickerRef).after(`<div class="${_vm.dimOverlayClass} overlay active"></div>`);
        },
        onClose: function () {
          _vm.removeDim();
        },
        onSelect: function (dateText, inst) {
          _vm.removeDim();

          _vm.getValue();
          _vm.$emit('input', _vm.localVal);
        },      
      };
      //console.debug(_vm.localOption);
      $(_vm.datePickerRef).datepicker(_vm.localOption).datepicker("setDate", initDate);

      _vm.getValue();
      //console.debug(_vm.localVal);
      _vm.$emit('input', _vm.localVal);
    },

    /**
     * Param -> localValue -> DatePicker
     */
    setValue(){
      const _vm = this;
      
      _vm.setParam();
      const newDate = moment(_vm.localVal);
      $(_vm.datePickerRef).datepicker("setDate", newDate.toDate());
    },

    /**
     * DatePicker -> localValue
     */
    getValue(){
      const _vm = this;

      const oldVal = _vm.localVal;

      const inputDatePicker = _vm.$refs.inputDatePicker;
      const dateObject = $(inputDatePicker).datepicker("getDate");
      const dateString = $.datepicker.formatDate("yy-mm-dd", dateObject);
      _vm.localVal = dateString; 

      _vm.$emit('input', _vm.localVal);
      
      if(oldVal != _vm.localVal){
        //console.log(`oldVal:${oldVal}, localVal:${_vm.localVal}`);
        _vm.$emit('change', _vm.localVal, oldVal);
      }
      
      return _vm.localVal;
    },

    removeDim(){
      $(`.${this.dimOverlayClass}`).remove();
    }

  }
};

</script>