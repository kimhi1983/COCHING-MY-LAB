//모바일 크롬 하단 툴바 문제
$(document).ready(function () {
  function adjustModalHeight() {
    var vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty("--vh", `${vh}px`);
  }

  window.addEventListener("resize", adjustModalHeight);
  adjustModalHeight();
});

//back-to-top
$(document).ready(function () {
  var btn = $("#back-to-top");

  $(window).scroll(function () {
    if ($(window).scrollTop() > 300) {
      btn.addClass("show");
    } else {
      btn.removeClass("show");
    }
  });

  btn.on("click", function (e) {
    e.preventDefault();
    $("html, body").animate(
      {
        scrollTop: 0,
      },
      "400"
    );
  });
});

//select
$(document).ready(function () {
  function disableCustomSelect(customSelect) {
    customSelect.addClass("disabled");
    customSelect.find(".custom-select-trigger").text(customSelect.find("select").attr("placeholder"));
    customSelect.find(".custom-option").attr("data-value", "");
    customSelect.find("select").prop("disabled", true);

    customSelect.find(".custom-select-trigger").off("click");
    customSelect.find(".custom-option").off("click");
  }

  function initializeCustomSelect(selectElement) {
    var classes = $(selectElement).attr("class"),
      id = $(selectElement).attr("id"),
      name = $(selectElement).attr("name");
    var template = '<div class="' + classes + '">';
    template += '<span class="custom-select-trigger">' + $(selectElement).attr("placeholder") + "</span>";
    template += '<div class="custom-options">';
    $(selectElement)
      .find("option")
      .each(function () {
        var value = $(this).attr("value");
        var isDisabled = $(this).is(":disabled");
        template += '<span class="custom-option ' + $(this).attr("class") + '" data-value="' + value + '" ' + (isDisabled ? "disabled" : "") + ">" + $(this).html() + "</span>";
      });
    template += "</div></div>";

    $(selectElement).wrap('<div class="custom-select-wrapper"></div>');
    $(selectElement).hide();
    $(selectElement).after(template);

    if ($(selectElement).is(":disabled")) {
      var customSelect = $(selectElement).parents(".custom-select");
      disableCustomSelect(customSelect);
    }
  }

  $(".custom-select").each(function () {
    initializeCustomSelect(this);
  });

  $(".custom-select-trigger").on("click", function () {
    var customSelect = $(this).parents(".custom-select");
    if (!customSelect.hasClass("disabled")) {
      $("html").one("click", function () {
        customSelect.removeClass("opened");
      });
      $(".custom-select").not(customSelect).removeClass("opened");
      customSelect.toggleClass("opened");
      event.stopPropagation();
    }
  });

  $(".custom-option").on("click", function () {
    if (!$(this).attr("disabled")) {
      var customSelectWrapper = $(this).parents(".custom-select-wrapper");
      customSelectWrapper.find("select").val($(this).data("value"));
      customSelectWrapper.find(".custom-options .custom-option").removeClass("selection");
      $(this).addClass("selection");
      customSelectWrapper.find(".custom-select").removeClass("opened");
      customSelectWrapper.find(".custom-select-trigger").text($(this).text());
    }
  });
});

// nicescroll js
$(document).ready(function () {
  const isMobile = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);

  if (!isMobile) {
    // 데스크톱에서만 nicescroll 적용
    $(".scroller").niceScroll();

    // 리사이즈 시 스크롤 재조정
    $(window).on("resize", function () {
      $(".scroller").getNiceScroll().resize();
    });

    // 스크롤 이벤트에서 다른 스크롤 방지
    $(".scroller").on("scroll", function (e) {
      e.stopPropagation(); // 다른 스크롤 이벤트가 영향을 미치지 않도록 방지
    });
  } else {
    // 모바일에서는 기본 스크롤 사용
    $(".scroller").css("overflow", "auto");
  }
});

//scroller-x js
$(document).ready(function () {
  // jQuery scrollbar 초기화
  $(".scroller-x").scrollbar();

  // 마우스 휠 이벤트로 가로 스크롤 동작 추가
  $(".scroller-x").on("mousewheel DOMMouseScroll", function (e) {
    var delta = e.originalEvent.wheelDelta || -e.originalEvent.detail;
    var scrollAmount = 30; // 스크롤 이동 거리 설정

    if (delta > 0) {
      // 마우스 휠을 위로 스크롤할 때 (왼쪽으로 이동)
      $(this).scrollLeft($(this).scrollLeft() - scrollAmount);
    } else {
      // 마우스 휠을 아래로 스크롤할 때 (오른쪽으로 이동)
      $(this).scrollLeft($(this).scrollLeft() + scrollAmount);
    }

    // 기본 동작을 막아 세로 스크롤이 작동하지 않게 함
    e.preventDefault();
  });
});

/*브라우저 스크롤 안보이게, 마우스 휠 또는 드래그, 터치시 x 스크롤 가능하게*/
$(document).ready(function () {
  const tagWrap = document.querySelector(".scroll-x-area");

  // 마우스 휠로 가로 스크롤
  tagWrap.addEventListener(
    "wheel",
    function (e) {
      if (e.deltaY !== 0) {
        e.preventDefault();
        tagWrap.scrollLeft += e.deltaY;
      }
    },
    { passive: false }
  );

  // 마우스 드래그로 가로 스크롤
  let isDown = false;
  let startX;
  let scrollLeft;

  tagWrap.addEventListener("mousedown", (e) => {
    isDown = true;
    tagWrap.classList.add("dragging");
    startX = e.pageX - tagWrap.offsetLeft;
    scrollLeft = tagWrap.scrollLeft;
  });
  tagWrap.addEventListener("mouseleave", () => {
    isDown = false;
    tagWrap.classList.remove("dragging");
  });
  tagWrap.addEventListener("mouseup", () => {
    isDown = false;
    tagWrap.classList.remove("dragging");
  });
  tagWrap.addEventListener("mousemove", (e) => {
    if (!isDown) return;
    e.preventDefault();
    const x = e.pageX - tagWrap.offsetLeft;
    const walk = (x - startX) * 1.5;
    tagWrap.scrollLeft = scrollLeft - walk;
  });
});

//child-scroll
$(document).ready(function () {
  // 자식 요소의 스크롤 이벤트 리스너
  $(".check-wrap").on("scroll", function (e) {
    // 현재 스크롤 위치
    var scrollTop = $(this).scrollTop();
    var scrollHeight = this.scrollHeight; // 자식 요소의 전체 높이
    var offsetHeight = this.offsetHeight; // 자식 요소의 현재 높이

    // 자식 요소가 스크롤 가능한 경우
    if (scrollTop + offsetHeight >= scrollHeight) {
      // 스크롤이 아래로 이동할 때 부모의 스크롤을 방지
      e.stopPropagation();
    } else if (scrollTop <= 0) {
      // 스크롤이 위로 이동할 때 부모의 스크롤을 방지
      e.stopPropagation();
    }
  });
});

//모바일 header scrolled
$(document).ready(function () {
  const targetDiv = $("header");
  let lastScrollTop = 0; // 마지막 스크롤 위치 저장 변수
  const triggerHeight = 200; // 스크롤 후에 클래스 적용

  // 스크롤 이벤트
  $(window).on("scroll", function () {
    const scrollTop = $(this).scrollTop(); // 현재 스크롤 위치

    // 화면 너비 확인 (모바일 이하에서만 작동)
    if ($(window).width() <= 767) {
      if (scrollTop > triggerHeight) {
        // 스크롤이 200 이상일 때만 아래/위 스크롤을 감지
        if (scrollTop > lastScrollTop) {
          // 아래로 스크롤 중일 때
          if (!targetDiv.hasClass("scrolled")) {
            targetDiv.addClass("scrolled"); // 클래스가 없으면 추가
          }
        } else {
          // 위로 스크롤 중일 때
          targetDiv.removeClass("scrolled"); // 클래스 제거
        }
      } else {
        // 스크롤이 200 이하일 때는 클래스 무조건 제거
        targetDiv.removeClass("scrolled");
      }

      // 현재 스크롤 위치를 저장
      lastScrollTop = scrollTop;
    }
  });

  // 리사이즈 이벤트로 반응형 처리
  $(window).on("resize", function () {
    if ($(window).width() > 767) {
      targetDiv.removeClass("scrolled"); // 모바일 이상 크기일 때 클래스 제거
    }
  });
});

/*link-box-sticky*/
$(document).ready(function () {
  var search = $(".link-box-sticky");
  $(window).scroll(function () {
    if ($(window).scrollTop() > 200) {
      search.addClass("sticky");
    } else {
      search.removeClass("sticky");
    }
  });
});

//link-box-sticky 1023 이하 sticky-m
$(document).ready(function () {
  const targetDiv = $(".link-box-sticky");
  let lastScrollTop = 0; // 마지막 스크롤 위치 저장 변수
  const triggerHeight = 200; // 스크롤 후에 클래스 적용

  // `scroller-x` 외부에서만 스크롤 이벤트를 처리하도록 설정
  $(window).on("scroll", function () {
    const scrollTop = $(this).scrollTop(); // 현재 스크롤 위치
    const windowWidth = $(window).width(); // 화면 너비

    // 화면 너비가 1023 이하일 때만 작동
    if (windowWidth <= 1023) {
      // 스크롤이 triggerHeight 이상일 때만 아래/위 스크롤을 감지
      if (scrollTop > triggerHeight) {
        // 아래로 스크롤 중일 때
        if (scrollTop > lastScrollTop) {
          if (!targetDiv.hasClass("sticky-m")) {
            targetDiv.addClass("sticky-m"); // 클래스가 없으면 추가
          }
        } else {
          // 위로 스크롤 중일 때
          if (targetDiv.hasClass("sticky-m")) {
            targetDiv.removeClass("sticky-m"); // 클래스 제거
          }
        }
      } else {
        // 스크롤이 triggerHeight 이하일 때는 클래스 무조건 제거
        if (targetDiv.hasClass("sticky-m")) {
          targetDiv.removeClass("sticky-m");
        }
      }
    }

    // 현재 스크롤 위치를 저장
    lastScrollTop = scrollTop;
  });

  // `scroller-x` 플러그인 초기화
  $(".scroller-x").scrollbar();

  // 리사이즈 이벤트로 반응형 처리
  $(window).on("resize", function () {
    if ($(window).width() > 1023) {
      targetDiv.removeClass("sticky-m"); // 화면 크기가 1023px 이상일 때 클래스 제거
    }
  });
});

//link-box-wrap 1023px 이하 scroller-x 적용안함
$(document).ready(function () {
  const $scrollerX = $(".link-box-wrap .scroller-x");

  // 스크롤바 플러그인 초기화 함수
  function initializeScrollbar() {
    $scrollerX.each(function () {
      $(this).scrollbar(); // 플러그인 적용
    });
  }

  // 스크롤바 플러그인 제거 함수
  function destroyScrollbar() {
    $scrollerX.each(function () {
      $(this).scrollbar("destroy"); // 플러그인 제거
    });
  }

  // 스크롤바 적용 및 제거 처리
  function handleScrollbar() {
    if ($(window).width() > 1023) {
      initializeScrollbar();
    } else {
      destroyScrollbar();
    }
  }

  // 페이지 로드 시 스크롤바 처리
  handleScrollbar();

  // 리사이즈 이벤트로 스크롤바 처리
  $(window).on("resize", function () {
    handleScrollbar();
  });
});

/*tabs*/
$(document).ready(function () {
  $(".tabs .tabs-nav a").click(function () {
    var is_active = $(this).parents("li").hasClass("active");

    if (!is_active) {
      $(this).parents("ul").find("li").removeClass("active");
      $(this).parents("li").addClass("active");

      var target = $(this).attr("href");
      $(target).addClass("active").siblings().removeClass("active");
    }
    return false;
  });
});

/* 약관동의 check all*/
$(document).ready(function () {
  // '전체 동의' 체크박스 클릭 시
  $("#checkall-agree").click(function () {
    if ($(this).prop("checked")) {
      $("input[name=chk-checkall-agree]").prop("checked", true);
    } else {
      $("input[name=chk-checkall-agree]").prop("checked", false);
    }
  });

  // 개별 체크박스 클릭 시
  $("input[name=chk-checkall-agree]").change(function () {
    // 개별 체크박스가 하나라도 체크되지 않은 경우 전체 체크박스 해제
    if ($("input[name=chk-checkall-agree]").not(":checked").length > 0) {
      $("#checkall-agree").prop("checked", false);
    } else {
      // 모두 체크된 경우 전체 체크박스 체크
      $("#checkall-agree").prop("checked", true);
    }
  });
});

/* board check all*/
$(document).ready(function () {
  $(".board-wrap").each(function () {
    const $section = $(this);

    $section.find(".checkall-board").click(function () {
      const isChecked = $(this).prop("checked");
      $section.find(".chk-board").prop("checked", isChecked);
    });

    $section.find(".chk-board").change(function () {
      const allChecked = $section.find(".chk-board").length === $section.find(".chk-board:checked").length;
      $section.find(".checkall-board").prop("checked", allChecked);
    });
  });
});

/*로고 업로드*/
$(document).ready(function () {
  const $fileInput = $("#fileInput");
  const $previewImage = $("#previewImage");
  const $deleteButton = $("#deleteButton");
  const $uploadText = $(".upload-text");
  const $dropArea = $(".drop-area");
  const $form = $(".upload-form"); // class를 사용하여 form 선택

  // 파일 선택 시 미리보기와 삭제 버튼 표시
  $fileInput.on("change", function () {
    const file = this.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        $previewImage.attr("src", e.target.result).show();
        $deleteButton.show();
        $uploadText.hide(); // 텍스트 숨기기
      };
      reader.readAsDataURL(file);
    }
  });

  // 삭제 버튼 클릭 시 이미지만 삭제하고 파일 입력 창은 열리지 않도록
  $deleteButton.on("click", function (e) {
    e.preventDefault(); // 폼의 기본 동작 방지
    e.stopPropagation(); // 이벤트 전파 방지

    $previewImage.hide();
    $deleteButton.hide();
    $uploadText.show(); // 텍스트 다시 보이기
    $fileInput.val(""); // 파일 입력 값 초기화
  });

  // 드래그 앤 드롭 또는 클릭 시 파일 입력 창 열기
  $dropArea.on("click", function () {
    if (!$previewImage.is(":visible")) {
      $fileInput.click(); // 파일 입력 창 열기
    }
  });

  // 드래그 앤 드롭 이벤트 처리
  $dropArea.on("dragover", function (e) {
    e.preventDefault(); // 드래그 오버 시 기본 동작 방지
  });

  $dropArea.on("drop", function (e) {
    e.preventDefault(); // 드롭 시 기본 동작 방지
    const files = e.originalEvent.dataTransfer.files;
    if (files.length > 0) {
      const file = files[0];
      const reader = new FileReader();
      reader.onload = function (e) {
        $previewImage.attr("src", e.target.result).show();
        $deleteButton.show();
        $uploadText.hide(); // 텍스트 숨기기
      };
      reader.readAsDataURL(file);
    }
  });
});

/*heart*/
$(document).ready(function () {
  $(".heart-wrap").click(function () {
    $(this).toggleClass("active");
  });
});

/*data-choice*/
$(document).ready(function () {
  $(".data-choice .item").click(function () {
    // .order-wrap 내의 .data-choice .item 클릭을 막음
    if (!$(this).closest(".order-wrap").length) {
      $(this).toggleClass("active");
    }
  });
});

/*filter-box*/
$(document).ready(function () {
  $(".filter-box .item").click(function () {
    $(this).css("display", "none");

    // 남은 item이 하나도 없으면 filter-box 숨기기
    if ($(".filter-box .item:visible").length === 0) {
      $(".filter-box").css("display", "none");
    }
  });
});

/*filter-check acd*/
$(document).ready(function () {
  $(".filter-check .filter-acd .title").click(function () {
    var $closest = $(this).closest(".filter-acd");

    if ($closest.hasClass("block")) {
      $closest.removeClass("block").removeClass("active");
    } else {
      $closest.toggleClass("active");
    }
  });
});

/*vs*/
$(document).ready(function () {
  $(".card-vs .vs").click(function () {
    // 현재 클릭된 항목의 상태를 먼저 토글
    $(this).toggleClass("active");

    // 선택된 .vs 요소의 개수를 클릭 후 다시 확인
    var selectedVs = $(".card-vs .vs.active").length;

    // 두 개 이상 선택 시 토스트 메시지 표시 및 현재 항목을 다시 비활성화
    if (selectedVs > 2) {
      $(this).removeClass("active"); // 세 번째 클릭한 항목 비활성화
      showToast("최대 2개의 항목만 선택 가능합니다.");
      return; // 함수 종료
    }

    // 선택된 항목이 정확히 2개일 때 버튼 클래스 변경
    if (selectedVs === 2) {
      $(".for-vs .btn-disabled").removeClass("btn-disabled").addClass("btn-primary");
    } else {
      // 선택된 항목이 2개 미만일 때 다시 버튼을 비활성화 상태로 변경
      $(".for-vs .btn-primary").removeClass("btn-primary").addClass("btn-disabled");
    }

    // .for-vs 요소에 관련된 동작
    $(".for-vs").addClass("active");
    $(".for-vs .body").css("display", "block");
    $(".for-vs").removeClass("acd");
  });

  // 토스트 메시지 표시 함수
  function showToast(message) {
    var toast = $('<div class="toast"></div>').text(message);
    $("body").append(toast);

    setTimeout(function () {
      toast.css("opacity", 1);
    }, 100);

    setTimeout(function () {
      toast.css("opacity", 0);
      setTimeout(function () {
        toast.remove();
      }, 500);
    }, 2000);
  }
});

/*for-vs*/
$(document).ready(function () {
  $(".for-vs .ic-arrow-down").click(function () {
    $(".for-vs").toggleClass("acd");
    $(".for-vs .body").slideToggle(); // 자연스러운 애니메이션으로 토글
  });
});

/*for-vs item remove*/
$(document).ready(function () {
  $(".for-vs .item .ic-close").click(function () {
    $(this).closest(".item").addClass("empty");
    $(this).closest(".inner").remove();
  });
});

/*for-vs 초기화*/
$(document).ready(function () {
  $(".for-vs .deselect").click(function () {
    $(".for-vs").removeClass("active");
    $(".card-vs .vs").removeClass("active");
  });
});

//comment-box
$(document).ready(function () {
  // comment-box 클래스가 있는 부모 요소에서 이벤트 위임
  $(document).on("input", ".comment-box textarea", function () {
    var $btn = $(this).closest(".comment-box").find("button");
    if ($(this).val().trim() !== "") {
      $btn.removeClass("btn-disabled").addClass("btn-primary");
    } else {
      $btn.removeClass("btn-primary").addClass("btn-disabled");
    }
  });
});

/*pw-change-wrap*/
$(".pw-change-wrap button").click(function () {
  $(this).closest(".pw-change-wrap").find(".input-wrap").addClass("active");
});

/*modal*/
$(".modal-open").click(function () {
  $("body").css("overflow", "hidden");
});

/*상단 X버튼 닫기*/
$(document).ready(function () {
  $(".modal-close").click(function () {
    $(".modal").fadeOut(300);
    $("body").css("overflow", "visible");
  });
});

/*하단닫기버튼*/
$(".bottom-modal-close").click(function () {
  $(".modal").fadeOut(300);
  $("body").css("overflow", "visible");
});

/*기본모달*/
$(".default-modal").click(function () {
  $(".for-default").fadeIn(300);
});

/*버튼두개모달*/
$(".double-modal").click(function () {
  $(".for-double").fadeIn(300);
});

/*타이틀 없음 */
$(".basic-modal").click(function () {
  $(".for-basic").fadeIn(300);
});

/*확인모달*/
$(".check-modal").click(function () {
  $(".for-check").fadeIn(300);
});

/*오류모달*/
$(".error-modal").click(function () {
  $(".for-error").fadeIn(300);
});

/*안내모달*/
$(".info-modal").click(function () {
  $(".for-info").fadeIn(300);
});

/*이용약관*/
$(".agree-modal").click(function () {
  $(".for-agree").fadeIn(300);
});

/*id-pw-find*/
$(".id-pw-find-modal").click(function () {
  $(".for-id-pw-find").fadeIn(300);
});

/*쪽지보내기*/
$(".send-modal").click(function () {
  $(".for-send").fadeIn(300);
});

/*쪽지보내기 input*/
$(".send-input-modal").click(function () {
  $(".for-send-input").fadeIn(300);
});

/*최근 검색어 삭제*/
$(".search-delete-modal").click(function () {
  $(".for-search-delete").fadeIn(300);
});

/*modal full*/
$(".modal-open-full").click(function () {
  $("body").css("overflow", "hidden");
});

/*상단 X버튼 닫기*/
$(document).ready(function () {
  $(".full-close").click(function () {
    $(".modal-full").removeClass("active");
    $("body").css("overflow", "visible");
  });
});
$(document).ready(function () {
  $(".bottom-modal-full-close").click(function () {
    $(".modal-full").removeClass("active");
    $("body").css("overflow", "visible");
  });
});

// 모달의 inner 외부 영역 클릭 시 모달 닫기
// $(document).ready(function () {
//   var modalFull = $('.modal-full');
//   $(document).mouseup(function (e) {
//     // modal이 active 상태일 때만 동작
//     if (modalFull.hasClass('active')) {
//       if (!modalFull.find('.inner').is(e.target) && modalFull.has(e.target).length === 0) {
//         modalFull.removeClass('active');
//         $('body').css('overflow', 'visible');
//       }
//     }
//   });
// });

/*menu*/
$(".menu-modal").click(function () {
  $(".for-menu").addClass("active");
});

/*filter*/
$(".filter-modal").click(function () {
  $(".for-filter").addClass("active");
});

/*card*/
$(".card-modal").click(function () {
  $(".for-card").addClass("active");
});

/*card-product*/
$(".card-product-modal").click(function () {
  $(".for-card-product").addClass("active");
});

/*vs-detail*/
$(".vs-detail-modal").click(function () {
  $(".for-vs-detail").addClass("active");
  $(".for-vs .body").slideToggle();
  $(".for-vs").addClass("acd");
});

/*request-data*/
$(".request-data-modal").click(function () {
  $(".for-request-data").addClass("active");
});

//user-wrap
$(document).ready(function () {
  $(".user-container .user-wrap, .user-container .for-user").hover(
    function () {
      $(".user-container .for-user").addClass("on");
    },
    function () {
      $(".user-container .for-user").removeClass("on");
    }
  );
});

//noti, hart dropdown 추가 가능
$(document).ready(function () {
  // 열기 버튼 클릭
  $(".toggle-container .toggle-btn").click(function () {
    var $container = $(this).closest(".toggle-container");
    var target = $container.data("target");

    // 모든 toggle-content 닫기
    $(".toggle-content").removeClass("on");

    // 해당 toggle-content 열기
    $container.find(".toggle-content.for-" + target).addClass("on");
  });

  // 닫기 버튼 클릭 (수정된 부분)
  $(".toggle-container .ic-close").click(function () {
    var $container = $(this).closest(".toggle-container");
    $container.find(".toggle-content").removeClass("on");
  });
});

// main search
$(document).ready(function () {
  const mainSearch = document.querySelector(".main-search");
  const input = document.querySelector(".main-search #searchInput");
  const deleteBtn = document.querySelector(".main-search .ic-delete");
  const hashBtn = document.querySelector(".main-search .ic-lg.ic-hash");
  const hashTag = document.querySelector(".main-search .hash");
  const tagLayer = document.querySelector(".main-search .tag-layer");
  const tagWrap = document.querySelector(".main-search .tag-wrap");
  const tagButton = document.querySelector(".main-search .tag");
  const autoLayer = document.querySelector(".main-search .auto-layer");
  const bodyContainer = document.querySelector(".main-search .search-list .body");
  const backBtn = document.querySelector(".main-search .ic-lg.ic-arrow-left-long");
  const modal = document.querySelector(".for-search-delete");
  const deleteAllBtn = document.querySelector(".for-search-delete .delete");

  // all-hidden 처리 함수
  function handleAllHidden() {
    const isMobile = window.innerWidth <= 767;
    const isFocused = mainSearch.classList.contains("focus");

    if (isMobile && isFocused) {
      document.body.classList.add("all-hidden");
      if (backBtn) backBtn.style.display = "inline-block";
    } else {
      document.body.classList.remove("all-hidden");
      if (backBtn) backBtn.style.display = "none";
    }
  }

  // input focus 시 focus 클래스 추가 및 all-hidden 처리
  input.addEventListener("focus", function () {
    mainSearch.classList.add("focus");
    handleAllHidden();

    if (hashBtn.classList.contains("active")) {
      tagLayer.style.display = "none";
      autoLayer.style.display = "none";
      return;
    }

    if (input.value.trim()) {
      autoLayer.style.display = "block";
      tagLayer.style.display = "none";
    } else {
      tagLayer.style.display = "block";
      autoLayer.style.display = "none";
    }
  });

  // 문서 클릭 시 main-search 바깥이면 focus 제거
  document.addEventListener("mousedown", function (e) {
    const isModalOpen = modal && window.getComputedStyle(modal).display !== "none";
    if (isModalOpen) return;

    if (!mainSearch.contains(e.target) && !e.target.closest(".item-delete") && e.target !== deleteAllBtn && !e.target.closest(".main-search")) {
      mainSearch.classList.remove("focus");
      tagLayer.style.display = "none";
      autoLayer.style.display = "none";
      // input.blur(); // 모바일 기기에서 input focus 아웃 시 키패드 내리기
      handleAllHidden();
    }
  });

  // input 입력 시 자동완성 레이어 처리
  input.addEventListener("input", function () {
    const keyword = input.value.trim();
    deleteBtn.style.display = keyword ? "inline-block" : "none";

    if (hashBtn.classList.contains("active")) {
      tagLayer.style.display = "none";
      autoLayer.style.display = "none";
      return;
    }

    if (keyword) {
      tagLayer.style.display = "none";
      autoLayer.style.display = "block";
    } else {
      autoLayer.style.display = "none";
      tagLayer.style.display = "block";
    }
  });

  // 삭제 버튼 클릭 시
  deleteBtn.addEventListener("mousedown", function (e) {
    e.preventDefault();
    input.value = "";
    deleteBtn.style.display = "none";
    input.focus();

    if (hashBtn.classList.contains("active")) {
      tagLayer.style.display = "none";
      autoLayer.style.display = "none";
    } else {
      autoLayer.style.display = "none";
      tagLayer.style.display = "block";
    }
  });

  // 해시 버튼 클릭 시
  hashBtn.addEventListener("click", function (e) {
    e.preventDefault();

    hashBtn.classList.toggle("active");
    const isActive = hashBtn.classList.contains("active");

    if (isActive) {
      tagLayer.style.display = "none";
      autoLayer.style.display = "none";
      tagButton.style.display = "none";
      hashTag.style.display = "inline-block";
      mainSearch.classList.add("hash-on");
    } else {
      hashTag.style.display = "none";
      autoLayer.style.display = input.value.trim() ? "block" : "none";
      tagLayer.style.display = input.value.trim() ? "none" : "block";
      tagButton.textContent = "";
      tagButton.style.display = "none";
      mainSearch.classList.remove("hash-on");
    }

    mainSearch.classList.add("focus");
    input.focus();
    handleAllHidden();

    const activeBtn = tagWrap.querySelector(".btn-primary");
    if (activeBtn) {
      activeBtn.classList.remove("btn-primary");
      activeBtn.classList.add("btn-gray");
    }
  });

  // 태그 선택 버튼 클릭 시
  tagWrap.querySelectorAll("button").forEach(function (btn) {
    btn.addEventListener("click", function () {
      const activeBtn = tagWrap.querySelector(".btn-primary");
      if (activeBtn) {
        activeBtn.classList.remove("btn-primary");
        activeBtn.classList.add("btn-gray");
      }

      tagButton.textContent = btn.textContent;
      tagButton.style.display = "inline-block";

      btn.classList.remove("btn-gray");
      btn.classList.add("btn-primary");

      mainSearch.classList.add("focus");
      input.focus();
      handleAllHidden();

      tagLayer.style.display = "block";
      autoLayer.style.display = "none";

      if (hashBtn.classList.contains("active")) {
        hashBtn.classList.remove("active");
        hashTag.style.display = "none";
      }
    });
  });

  // tag 버튼 클릭 시 초기화
  tagButton.addEventListener("click", function () {
    const activeBtn = tagWrap.querySelector(".btn-primary");
    if (activeBtn) {
      activeBtn.classList.remove("btn-primary");
      activeBtn.classList.add("btn-gray");
    }

    tagButton.textContent = "";
    tagButton.style.display = "none";

    mainSearch.classList.add("focus");
    input.focus();
    handleAllHidden();

    const keyword = input.value.trim();

    if (!hashBtn.classList.contains("active")) {
      if (keyword) {
        tagLayer.style.display = "none";
        autoLayer.style.display = "block";
      } else {
        tagLayer.style.display = "block";
        autoLayer.style.display = "none";
      }
    }
  });

  // 개별 아이템 삭제
  bodyContainer.addEventListener("click", function (e) {
    if (e.target.closest(".item-delete")) {
      const item = e.target.closest(".item");
      if (item) item.remove();
      e.stopPropagation();
    }
  });

  // 전체 삭제 버튼 클릭
  if (deleteAllBtn) {
    deleteAllBtn.addEventListener("click", function (e) {
      e.preventDefault();
      const allItems = bodyContainer.querySelectorAll(".item");
      allItems.forEach((item) => item.remove());
      e.stopPropagation();
    });
  }

  // 초기 설정
  deleteBtn.style.display = "none";
  autoLayer.style.display = "none";

  if (hashBtn.classList.contains("active")) {
    tagLayer.style.display = "none";
    tagButton.style.display = "none";
    hashTag.style.display = "inline-block";
  } else {
    tagLayer.style.display = "none";
    tagButton.style.display = "none";
    hashTag.style.display = "none";
  }

  // 뒤로가기 버튼 클릭 시
  if (backBtn) {
    backBtn.addEventListener("click", function () {
      mainSearch.classList.remove("focus");
      input.blur();
      autoLayer.style.display = "none";
      tagLayer.style.display = "none";
      handleAllHidden();
    });

    backBtn.style.display = "none"; // 초기 숨김
  }

  // 리사이즈 대응
  window.addEventListener("resize", function () {
    handleAllHidden();
  });
});

/*nav gnb-acd*/
$(document).ready(function () {
  $("nav .gnb-acd .title").click(function () {
    var $closest = $(this).closest(".gnb-acd");

    if ($closest.hasClass("block")) {
      $closest.removeClass("block").removeClass("active");
    } else {
      $closest.toggleClass("active");
    }
  });
});

/*nav toggle*/
$(document).ready(function () {
  $("header .left .menu-wrap").click(function () {
    $("nav").toggleClass("active");
    $("section").toggleClass("active");
  });
});

//Co search
$(document).ready(function () {
  const $CosearchInput = $("#CosearchInput");
  const $CodeleteButton = $(".Co-search .ic-delete");
  const $ComainSearch = $(".Co-search");
  const $ColayerLinks = $(".Co-search .search-layer a ");
  const $Coinner = $(".Co-search .inner");

  // 처음에 ic-delete 버튼 숨기기
  $CodeleteButton.hide();

  // Function to check screen size
  const isSmallScreen = () => window.matchMedia("(max-width: 1023px)").matches;

  // Function to handle class toggling based on screen size
  const handleResize = () => {
    if (isSmallScreen()) {
      if ($ComainSearch.hasClass("focus")) {
        $Coinner.addClass("focused");
      }
    } else {
      $Coinner.removeClass("focused");
    }
  };

  // Focus event for input
  $CosearchInput.on("focus", function () {
    const query = $CosearchInput.val().trim();
    if (query !== "") {
      $CodeleteButton.show(); // 값이 있을 때만 ic-delete 버튼 표시
    }
    $ComainSearch.addClass("focus");
    if (isSmallScreen()) {
      $Coinner.addClass("focused");
    }
  });

  // 1023px 이상일 때 .inner와 body에서 클래스 제거
  if (!isSmallScreen()) {
    $Coinner.removeClass("focused");
  }

  // Input event to toggle delete button visibility
  $CosearchInput.on("input", function () {
    const query = $CosearchInput.val().trim();
    if (query !== "") {
      $CodeleteButton.show(); // 입력 시 ic-delete 버튼 표시
    } else {
      $CodeleteButton.hide(); // 입력이 없으면 ic-delete 버튼 숨김
    }

    // Filter and highlight text
    const searchQuery = query.toLowerCase();
    $ColayerLinks.each(function () {
      const $link = $(this);
      const text = $link.text().trim();
      if (text.toLowerCase().includes(searchQuery) && searchQuery !== "") {
        const highlightedText = text.replace(new RegExp(`(${searchQuery})`, "gi"), '<span class="highlight">$1</span>');
        $link.html(highlightedText).removeClass("hidden");
      } else {
        $link.addClass("hidden");
      }
    });
  });

  // Click event for delete button to clear input
  $CodeleteButton.on("click", function () {
    $CosearchInput.val(""); // 입력값 삭제
    $CodeleteButton.hide(); // ic-delete 버튼 숨김
    // 유지 포커스와 상태
    $CosearchInput.focus(); // 포커스 유지
    $ColayerLinks.addClass("hidden"); // 검색 레이어 링크 숨김
  });

  // Click event for links to set input value
  $ColayerLinks.on("click", function () {
    const text = $(this).text().trim();
    $CosearchInput.val(text);
    $CodeleteButton.hide(); // 링크 클릭 시 ic-delete 버튼 숨김
  });

  // Document-wide click event to handle class removal
  $(document).on("click", function (e) {
    if (isSmallScreen()) {
      // 1023px 이하일 때: 특정 요소를 클릭했을 때만 클래스 제거
      if (!$(e.target).closest($CosearchInput).length && !$(e.target).is($CodeleteButton)) {
        $ComainSearch.removeClass("focus");
        $Coinner.removeClass("focused");
      }
    } else {
      // 1023px 이상일 때: 모든 곳을 클릭하면 클래스 제거
      if (!$(e.target).closest($CosearchInput).length && !$(e.target).is($CodeleteButton)) {
        $ComainSearch.removeClass("focus");
        $Coinner.removeClass("focused");
      }
    }
  });
});

//hash-tag 말줄임
$(document).ready(function () {
  function updateHashTag($tagWrap) {
    const originalTags = $tagWrap.data("original-tags");

    if (!originalTags) return;

    $tagWrap.empty();

    const $measureBox = $("<div></div>")
      .css({
        visibility: "hidden",
        position: "absolute",
        top: 0,
        left: 0,
        width: $tagWrap.width(),
        whiteSpace: "nowrap",
      })
      .appendTo("body");

    let totalWidth = 0;
    let maxWidth = $tagWrap.innerWidth();
    let showCount = 0;

    for (let i = 0; i < originalTags.length; i++) {
      const $tag = $(originalTags[i].cloneNode(true));
      $measureBox.append($tag);
      totalWidth += $tag.outerWidth(true);
      if (totalWidth + 0 > maxWidth) break;
      showCount++;
    }

    $measureBox.remove();

    for (let i = 0; i < showCount; i++) {
      $tagWrap.append($(originalTags[i].cloneNode(true)));
    }

    if (showCount < originalTags.length) {
      const $ellipsis = $("<span>...</span>").css({
        fontSize: "12px",
        color: "#2455F4",
      });
      $tagWrap.append($ellipsis);
    }
  }

  $(".hash-tag").each(function () {
    const $tagWrap = $(this);
    const originalTags = $tagWrap.find("a").toArray();
    $tagWrap.data("original-tags", originalTags);

    // 최초 1번 실행
    updateHashTag($tagWrap);

    // ResizeObserver 등록
    const resizeObserver = new ResizeObserver(() => {
      updateHashTag($tagWrap);
    });

    resizeObserver.observe($tagWrap[0]);
  });
});

/*view-tabs*/
$(document).ready(function () {
  // 초기: 첫 번째 탭 활성화
  $(".view-tabs a").first().addClass("active");

  // 반응형 offset gap 계산
  function getOffsetGap() {
    return window.innerWidth <= 767 ? 60 : 80;
  }

  // 탭 클릭 시 부드러운 스크롤 이동
  $(".view-tabs a").on("click", function (e) {
    e.preventDefault();
    var offsetGap = getOffsetGap();
    var targetId = $(this).attr("href");
    var scrollTarget = $(targetId).offset().top - $(".view-tabs").outerHeight() - offsetGap;

    $("html, body").animate({ scrollTop: scrollTarget }, 400);
  });

  // 스크롤 시 섹션 감지
  $(window).on("scroll", function () {
    var scrollTop = $(window).scrollTop();
    var offsetGap = getOffsetGap();
    var tabHeight = $(".view-tabs").outerHeight();
    var found = false;

    $(".base-view-content > div").each(function (i) {
      var sectionTop = $(this).offset().top - tabHeight - offsetGap;
      var sectionHeight = $(this).outerHeight();
      var sectionId = $(this).attr("id");

      // 현재 섹션이 보이기 시작하면 활성화
      if (scrollTop >= sectionTop - 1) {
        $(".view-tabs a").removeClass("active");
        $('.view-tabs a[href="#' + sectionId + '"]').addClass("active");
        found = true;
      }
    });

    // 맨 아래 도달 시: 마지막 섹션 강제 활성화
    var nearBottom = $(window).scrollTop() + $(window).height() >= $(document).height() - 10;
    if (nearBottom) {
      var lastId = $(".base-view-content > div").last().attr("id");
      $(".view-tabs a").removeClass("active");
      $('.view-tabs a[href="#' + lastId + '"]').addClass("active");
    }
  });
});

/*view-tabs sticky*/
$(document).ready(function () {
  const viewtabs = $(".view-tabs");
  let lastScrollTop = 0; // 마지막 스크롤 위치 저장 변수
  const viewtabsHeight = 200; // sticky가 시작되는 스크롤 위치 기준

  $(window).on("scroll", function () {
    const scrollTop = $(this).scrollTop();

    // 모바일 이하 너비 체크
    if ($(window).width() <= 767) {
      if (scrollTop > viewtabsHeight) {
        if (scrollTop > lastScrollTop) {
          // 아래로 스크롤 중일 때 sticky 추가
          if (!viewtabs.hasClass("sticky")) {
            viewtabs.addClass("sticky");
          }
        } else {
          // 위로 스크롤 중일 때 sticky 제거
          if (viewtabs.hasClass("sticky")) {
            viewtabs.removeClass("sticky");
          }
        }
      } else {
        // 스크롤 위치 200 이하일 땐 무조건 sticky 제거
        if (viewtabs.hasClass("sticky")) {
          viewtabs.removeClass("sticky");
        }
      }

      lastScrollTop = scrollTop;
    }
  });
});

/*logo upload*/
$(document).ready(function () {
  const $fileInput = $("#fileInput");
  const $previewImage = $("#previewImage");
  const $deleteButton = $("#deleteButton");
  const $uploadText = $(".upload-text");
  const $dropArea = $(".drop-area");
  const $form = $(".upload-form"); // class를 사용하여 form 선택

  // 파일 선택 시 미리보기와 삭제 버튼 표시
  $fileInput.on("change", function () {
    const file = this.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        $previewImage.attr("src", e.target.result).show();
        $deleteButton.show();
        $uploadText.hide(); // 텍스트 숨기기
      };
      reader.readAsDataURL(file);
    }
  });

  // 삭제 버튼 클릭 시 이미지만 삭제하고 파일 입력 창은 열리지 않도록
  $deleteButton.on("click", function (e) {
    e.preventDefault(); // 폼의 기본 동작 방지
    e.stopPropagation(); // 이벤트 전파 방지

    $previewImage.hide();
    $deleteButton.hide();
    $uploadText.show(); // 텍스트 다시 보이기
    $fileInput.val(""); // 파일 입력 값 초기화
  });

  // 드래그 앤 드롭 또는 클릭 시 파일 입력 창 열기
  $dropArea.on("click", function () {
    if (!$previewImage.is(":visible")) {
      $fileInput.click(); // 파일 입력 창 열기
    }
  });

  // 드래그 앤 드롭 이벤트 처리
  $dropArea.on("dragover", function (e) {
    e.preventDefault(); // 드래그 오버 시 기본 동작 방지
  });

  $dropArea.on("drop", function (e) {
    e.preventDefault(); // 드롭 시 기본 동작 방지
    const files = e.originalEvent.dataTransfer.files;
    if (files.length > 0) {
      const file = files[0];
      const reader = new FileReader();
      reader.onload = function (e) {
        $previewImage.attr("src", e.target.result).show();
        $deleteButton.show();
        $uploadText.hide(); // 텍스트 숨기기
      };
      reader.readAsDataURL(file);
    }
  });
});

/*file-upload 함수*/
function initFileUploader(uploadArea) {
  if (!uploadArea) return;

  const fileInput = uploadArea.querySelector(".file-input");
  const fileList = uploadArea.querySelector(".file-list");

  // placeholderText가 이미 있으면 중복 생성 방지
  if (!uploadArea.querySelector(".placeholder-text")) {
    const placeholderText = document.createElement("p");
    placeholderText.textContent = "파일을 마우스로 끌어 오거나 클릭하세요";
    placeholderText.className = "placeholder-text";
    uploadArea.appendChild(placeholderText);
  }
  const placeholderText = uploadArea.querySelector(".placeholder-text");

  let filesArray = [];

  uploadArea.addEventListener("dragover", function (event) {
    event.preventDefault();
    uploadArea.classList.add("dragging");
  });

  uploadArea.addEventListener("dragleave", function (event) {
    event.preventDefault();
    uploadArea.classList.remove("dragging");
  });

  uploadArea.addEventListener("drop", function (event) {
    event.preventDefault();
    uploadArea.classList.remove("dragging");
    const files = Array.from(event.dataTransfer.files);
    handleFiles(files);
  });

  uploadArea.addEventListener("click", function (event) {
    if (!fileInput.contains(event.target)) {
      fileInput.click();
    }
  });

  fileInput.addEventListener("change", function () {
    const files = Array.from(fileInput.files);
    handleFiles(files);
    fileInput.value = "";
  });

  function handleFiles(files) {
    if (files.length > 0) {
      placeholderText.style.display = "none";
    }

    files.forEach((file) => {
      filesArray.push(file);

      const fileItem = document.createElement("div");
      fileItem.className = "file-item";

      const fileWrap = document.createElement("div");
      fileWrap.className = "file-wrap";

      const fileName = document.createElement("div");
      fileName.className = "file-name";

      const lastDotIndex = file.name.lastIndexOf(".");
      const nameWithoutExtension = lastDotIndex > 0 ? file.name.substring(0, lastDotIndex) : file.name;
      const extension = lastDotIndex > 0 ? file.name.substring(lastDotIndex) : "";

      const fileNameText = document.createElement("span");
      fileNameText.textContent = nameWithoutExtension;

      const fileExtension = document.createElement("span");
      fileExtension.className = "file-extension";
      fileExtension.textContent = extension;

      fileName.appendChild(fileNameText);
      fileName.appendChild(fileExtension);

      const fileSize = document.createElement("div");
      fileSize.className = "file-size";
      fileSize.textContent = `(${(file.size / 1024).toFixed(2)} KB)`;

      fileWrap.appendChild(fileName);
      fileWrap.appendChild(fileSize);

      const deleteButton = document.createElement("button");
      deleteButton.className = "delete-button";

      deleteButton.addEventListener("click", function (event) {
        event.stopPropagation();
        const index = filesArray.indexOf(file);
        if (index > -1) {
          filesArray.splice(index, 1);
        }
        fileList.removeChild(fileItem);
        if (fileList.children.length === 0) {
          placeholderText.style.display = "block";
        }
      });

      fileItem.appendChild(fileWrap);
      fileItem.appendChild(deleteButton);
      fileList.appendChild(fileItem);
    });
  }
}

//comment comment-type, sample-type
$(document).ready(function () {
  // 수정 버튼 클릭 시
  $(document).on("click", ".btn-edit", function () {
    var $item = $(this).closest(".inner");
    var originalItem = $item.clone(true, true);
    $item.data("original", originalItem);

    var $container = $(this).closest(".more-container");
    var editForm = "";

    // 타입에 따라 다른 폼 적용
    if ($container.hasClass("comment-type")) {
      editForm = `
        <form>
          <div class="comment-edit-wrap">
            <div class="comment-box">
              <textarea rows="5" placeholder="댓글을 입력해 주세요"></textarea>
              <div class="bottom">
                <div class="checkbox">
                  <input id="secret" type="checkbox" />
                  <label for="secret" class="checkbox-label">비밀글</label>
                </div>
                <div class="right">
                  <button type="button" class="btn btn-sm btn-gray-outline btn-cancel">취소</button>
                  <button type="button" class="btn btn-sm btn-primary">수정</button>
                </div>
              </div>
            </div>
          </div>
        </form>`;
    } else if ($container.hasClass("sample-type")) {
      editForm = `
        <form class="sample-editor">
          <div class="comment-box">
            <textarea rows="3" placeholder="댓글을 입력해 주세요"></textarea>
            <div class="upload-wrap">
              <div class="upload-area">
                <input type="file" class="file-input" multiple />
                <div class="file-list"></div>
              </div>
            </div>
            <div class="bottom">
              <div class="datepicker-wrap">
                <span class="to">샘플발송 <span class="m-none">예정일</span></span>
                <input type="text" name="dateBox" class="datepicker" />
              </div>
              <div class="right">
                <button type="button" class="btn btn-sm btn-gray-outline btn-cancel">취소</button>
                <button type="button" class="btn btn-sm btn-primary">수정</button>
              </div>
            </div>
          </div>
        </form>`;
    }

    $item.html(editForm);
    $(this).closest(".more-container").find(".for-more").removeClass("on");

    // sample-type일 경우 datepicker와 파일업로더 초기화
    if ($container.hasClass("sample-type")) {
      $item.find(".datepicker").datepicker({
        dateFormat: "yy-mm-dd",
        buttonText: "날짜 선택",
        showMonthAfterYear: true,
        changeMonth: true,
        changeYear: true,
        showButtonPanel: true,
        monthNames: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        monthNamesShort: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        dayNames: ["(일)", "(월)", "(화)", "(수)", "(목)", "(금)", "(토)"],
        dayNamesMin: ["일", "월", "화", "수", "목", "금", "토"],
      });

      // 오늘 날짜로 설정
      $item.find(".datepicker").datepicker("setDate", "today");

      // 수정폼 내에 있는 upload-area 하나만 초기화
      initFileUploader($item.find(".upload-area")[0]);
    }
  });

  // 이미 있는 파일업로드 초기화
  $(".upload-area").each(function () {
    initFileUploader(this);
  });

  // 취소 버튼 클릭 시
  $(document).on("click", ".btn-cancel", function () {
    var $item = $(this).closest(".inner");
    var originalItem = $item.data("original");

    if (originalItem) {
      // 원본 복사본 만들어서 교체
      var $clone = originalItem.clone(true, true);
      $item.replaceWith($clone);

      // 교체한 새 요소에서 파일 업로더 초기화
      $clone.find(".upload-area").each(function () {
        initFileUploader(this);
      });
    }
    $(this).closest(".more-container").find(".for-more").removeClass("on");
  });

  // 댓글 토글
  $(document).on("click", ".btn-comment", function () {
    var $replyBox = $(this).closest(".inner").find(".reply-box");
    $replyBox.toggleClass("active");
  });

  // 더보기 메뉴 열기
  $(document).on("click", ".more-container .more-wrap", function () {
    $(this).siblings(".for-more").addClass("on");
  });

  // input 클릭 시
  $(document).on("click", ".send-input-modal", function (e) {
    e.stopPropagation();
    var $moreContainer = $(this).closest(".more-container");
    $moreContainer.find(".for-more").removeClass("on");
    $(".for-send-input").fadeIn(300);
  });

  // 더보기 메뉴에서 a 클릭 시 닫기
  $(document).on("click", ".more-container .for-more a", function () {
    $(this).closest(".for-more").removeClass("on");
  });

  // 바깥 클릭 시 더보기 닫기
  $(document).on("click", function (e) {
    if (!$(e.target).closest(".more-container").length) {
      $(".for-more").removeClass("on");
    }
  });
});

/*업로드 파일열기 클릭시 모달 열기*/
$(document).ready(function () {
  $(document).on("change", ".btn-camera .img-upload", function () {
    if (this.files.length > 0) {
      $(".for-profile-upload").fadeIn(300);
    }
  });
});
