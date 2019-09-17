RATE_LIMIT = 5000;
LIMIT_COUNT = 3000;

$(document).ready(function () {
    $(".markdown-left").keyup(function () {
        if (RATE_LIMIT < LIMIT_COUNT) {
            textareaBackgroundColorTurn();
            return false;
        }
        else textareaTransper(gitHubApiRateLimit());
    });

    $(".markdown-right").click(function () {
        if (RATE_LIMIT > LIMIT_COUNT) return false;
        else textareaTransper(gitHubApiRateLimit());
    });

    $(".api-count").html(RATE_LIMIT);
});

// <tab> 키 허용.
$(".markdown-textarea").keydown(function (e) {
    if (e.keyCode === 9) { // tab was pressed
        // get caret position/selection
        var start = this.selectionStart;
        var end = this.selectionEnd;

        var $this = $(this);
        var value = $this.val();

        // set textarea value to: text before caret + tab + text after caret
        $this.val(value.substring(0, start)
            + "\t"
            + value.substring(end));

        // put caret at right position again (add one for the tab)
        this.selectionStart = this.selectionEnd = start + 1;

        // prevent the focus lose
        e.preventDefault();
    }

    if(e.keyCode === 13) {
        // get caret position/selection
        var start = this.selectionStart;
        var end = this.selectionEnd;

        var $this = $(this);
        var value = $this.val();

        // set textarea value to: text before caret + tab + text after caret
        $this.val(value.substring(0, start)
            + "\n"
            + value.substring(end));

        // put caret at right position again (add one for the tab)
        this.selectionStart = this.selectionEnd = start + 1;

        // prevent the focus lose
        e.preventDefault();
    }
});

function gitHubApiRateLimit() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/getRateLimit",
        data: null,
        cache: false,
        timeout: 600000,
        success: function (data) {
            RATE_LIMIT = data;
            $(".api-count").html(RATE_LIMIT);

            console.log("SUCCESS: ", RATE_LIMIT);
        },
        error: function (e) {
            var json = e.responseText;

            $(".api-count").html(json);
        }
    })
}

// markdown rendering
function textareaTransper(callback) {
    var text = {"readme-text": $(".markdown-textarea").val()};

    // var str = text["readme-text"];
    // console.log("enter 확인: " ,  str);
    //
    // str = str.replace(/(?:\r\n|\r|\n)/g, '<br />');
    // console.log("enter 확인: " ,  str);

    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/rendering",
        data: text,
        cache: false,
        timeout: 600000,
        success: function (data) {
            $(".markdown-right").html(data);

            console.log("SUCESS: ", data);

            callback;
        },
        error: function (e) {
            var json = e.responseText;

            $(".markdown-right").html(json);
        }
    })
}

// textarea 내용 복사
function copy_text() {
    var copyText = document.getElementsByClassName("markdown-textarea");
    copyText[0].select();
    document.execCommand("copy");
    alert("Copied the text");
}

function textareaBackgroundColorTurn() {
    var markdownRightTag = document.getElementsByClassName("markdown-right")[0];
    markdownRightTag.style.backgroundColor = "#fadddb";
}