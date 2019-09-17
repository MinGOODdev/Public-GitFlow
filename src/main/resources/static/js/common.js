$(function() {

    $("[data-confirm-delete]").click(function() {
        return confirm("지원서를 삭제하시겠습니까?");
    })

    $("[data-confirm-submit]").click(function () {
        return confirm("지원서를 최종 제출하시겠습니까?\n" + "최종 제출 시 수정하실 수 없습니다.");
    })

})