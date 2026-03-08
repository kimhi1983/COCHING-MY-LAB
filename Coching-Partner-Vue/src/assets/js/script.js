/*back-to-top */
$(document).ready(function () {
  var btn = $("#back-to-top");
  var footer = $("footer");

  $(window).scroll(function () {
    var scrollPosition = $(window).scrollTop();
    var windowHeight = $(window).height();
    var footerOffset = footer.offset().top;

    // 스크롤이 300px 이상일 때 버튼 표시
    if (scrollPosition > 300) {
      btn.addClass("show");
    } else {
      btn.removeClass("show");
    }

    // 버튼이 footer에 닿으면 고정
    if (scrollPosition + windowHeight >= footerOffset) {
      btn.css({
        position: "absolute",
        top: footerOffset - btn.outerHeight() - 20, // footer에 닿기 전에 멈춤 (20px 여백)
      });
    } else {
      btn.css({
        position: "fixed",
        bottom: "20px", // 화면 아래에서 20px 위치에 고정
        top: "auto",
      });
    }
  });

  // 버튼 클릭 시 위로 스크롤
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

//nicescroll js
$(document).ready(function () {
  $(".scroller").niceScroll({
    horizrailenabled: false, // 가로 스크롤바 비활성화
  });

  // 리사이즈 이벤트 핸들러
  $(window).on("resize", function () {
    $(".scroller").getNiceScroll().resize(); // 스크롤바 리사이즈
  });
});

/* board check all*/
$(document).ready(function () {
  // '전체 동의' 체크박스 클릭 시
  $("#checkall-board").click(function () {
    if ($(this).prop("checked")) {
      $("input[name=chk-checkall-board]").prop("checked", true);
    } else {
      $("input[name=chk-checkall-board]").prop("checked", false);
    }
  });

  // 개별 체크박스 클릭 시
  $("input[name=chk-checkall-board]").change(function () {
    // 개별 체크박스가 하나라도 체크되지 않은 경우 전체 체크박스 해제
    if ($("input[name=chk-checkall-board]").not(":checked").length > 0) {
      $("#checkall-board").prop("checked", false);
    } else {
      // 모두 체크된 경우 전체 체크박스 체크
      $("#checkall-board").prop("checked", true);
    }
  });
});

/*file-upload*/
$(document).ready(function () {
  const uploadArea = document.getElementById("uploadArea");
  const fileInput = document.getElementById("fileInput");
  const fileList = document.getElementById("fileList");
  const placeholderText = document.createElement("p");

  let filesArray = [];

  placeholderText.textContent = "파일을 마우스로 끌어 오거나 클릭하세요";
  uploadArea.appendChild(placeholderText);

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
    if (event.target === uploadArea || event.target === placeholderText) {
      fileInput.click();
    }
  });

  fileInput.addEventListener("change", function () {
    const files = Array.from(fileInput.files);
    handleFiles(files);
    fileInput.value = ""; // 파일 첨부 창이 동일 파일 선택 가능하도록 초기화
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

      // 파일명과 확장명을 분리하여 텍스트 설정
      const nameWithoutExtension = file.name.substring(0, file.name.lastIndexOf("."));
      const extension = file.name.substring(file.name.lastIndexOf("."));

      const fileNameText = document.createElement("span");
      fileNameText.textContent = nameWithoutExtension;

      const fileExtension = document.createElement("span");
      fileExtension.className = "file-extension";
      fileExtension.textContent = extension;

      fileName.appendChild(fileNameText);
      fileName.appendChild(fileExtension);

      const fileSize = document.createElement("div");
      fileSize.className = "file-size";
      // 파일 사이즈를 KB 단위로 포맷팅하여 소수점 2자리까지 표시
      fileSize.textContent = `(${(file.size / 1023).toFixed(2)} KB)`;

      fileWrap.appendChild(fileName);
      fileWrap.appendChild(fileSize);

      const deleteButton = document.createElement("button");
      // deleteButton.textContent = 'Delete';
      deleteButton.className = "delete-button";

      deleteButton.addEventListener("click", function (event) {
        event.stopPropagation(); // 클릭 이벤트가 상위 요소로 전파되지 않도록 막음
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
});

/*account state-use*/
$(document).ready(function () {
  $(".badge-use.state-use").click(function () {
    $(this).toggleClass("active");

    if ($(this).hasClass("active")) {
      $(this).text("사용");
    } else {
      $(this).text("해지");
    }
  });
});

/*account state-invite*/
$(document).ready(function () {
  $(".badge-use.state-invite").click(function () {
    $(this).toggleClass("active");

    if ($(this).hasClass("active")) {
      $(this).text("초대중");
    } else {
      $(this).text("초대취소");
    }
  });
});

/*base-add*/
$(document).ready(function () {
  // 기존 select 요소에 chosen 초기화
  $(".chosen-select").chosen();

  // 이벤트 위임을 통해 동적으로 추가된 요소 처리
  $(document).on("click", ".ic-plus-add", function () {
    // 기존 chosen 관련 DOM 제거
    var itemToClone = $(this).closest(".item");
    itemToClone.find(".chosen-select").chosen("destroy"); // chosen 플러그인 해제

    // item을 복제
    var clonedItem = itemToClone.clone();

    // 복제한 후 chosen 플러그인 다시 초기화
    itemToClone.find(".chosen-select").chosen(); // 원래 선택박스 다시 chosen 적용

    // 복제된 chosen select의 값을 초기화
    clonedItem.find(".chosen-select").val("").trigger("chosen:updated");

    // 버튼을 plus에서 minus로 변경
    clonedItem.find(".ic-plus-add").removeClass("ic-plus-add").addClass("ic-minus-add");

    // 현재 요소 뒤에 복제된 item을 추가
    $(this).closest(".item").after(clonedItem);

    // 새로 추가된 select 요소에 chosen을 다시 초기화
    clonedItem.find(".chosen-select").chosen();
  });

  // minus 버튼 클릭 시 item 삭제 처리
  $(document).on("click", ".ic-minus-add", function () {
    // 해당 item을 삭제
    $(this).closest(".item").remove();
  });
});

//admin lnb
$(document).ready(function () {
  $("nav .btn-acd").click(function () {
    $("nav").toggleClass("active");
    $(".wrap").toggleClass("active");
  });
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

/*기본모달*/
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

/*구비서류*/
$(".document-modal").click(function () {
  $(".for-document").fadeIn(300);
});

/*등록자료*/
$(".data-modal").click(function () {
  $(".for-data").fadeIn(300);
});

/*엑셀업로드*/
$(".upload-modal").click(function () {
  $(".for-upload").fadeIn(300);
});

/*담당자 변경*/
$(".user-change-modal").click(function () {
  $(".for-user-change").fadeIn(300);
});

/*이미지 업로드*/
$(".img-upload-modal").click(function () {
  $(".for-img-upload").fadeIn(300);
});

/*이미지 닫기*/
$(".img-upload-close").click(function () {
  $(".for-img-upload").fadeOut(300);
  $("body").css("overflow", "visible");
});

/*계정초대*/
$(".invite-modal").click(function () {
  $(".for-invite").fadeIn(300);
});

/*업로드 파일열기 클릭시 에디터 모달 열기*/
$(document).ready(function () {
  $(document).on("change", ".btn-upload .img-upload", function () {
    if (this.files.length > 0) {
      $(".for-img-upload-edit").fadeIn(300);
    }
  });
});

/*에디터 모달 닫기*/
$(".img-upload-edit-close").click(function () {
  $(".for-img-upload-edit").fadeOut(300);
});

/*이미지 업로드 item 클릭 시시*/
$(document).ready(function () {
  $(document).on("click", ".img-type .item", function () {
    $(".item").removeClass("active");
    $(this).addClass("active");
  });
});

//계정초대
$(document).ready(function () {
  const items = document.querySelectorAll(".account-invite .item");

  items.forEach(function (item) {
    item.addEventListener("click", function () {
      items.forEach(function (el) {
        el.classList.remove("active");
      });

      this.classList.add("active");
    });
  });
});
