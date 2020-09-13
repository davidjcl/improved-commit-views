$(document).ready(function() {
	$(':text').keyup(function () {
		 
	    $(':text').each(function () {
	        if ($(this).val() == '') {
	            $('.action-button').attr('disabled', 'disabled');
	            return false;
	        }
	        else {
	            $('.action-button').attr('disabled', false);
	        }
	    });
	           
	});
});



function createPatchBox(changedFileName, changedFilePatch) {
	
	var diffs = "--- a/" + changedFileName + '\n+++ b/' + changedFileName + '\n' + changedFilePatch;
	
	document.getElementById(changedFileName).innerHTML = Diff2Html.getPrettyHtml(diffs, {
	          inputFormat: "diff",
	          showFiles: false,
	          matching: "lines",
	          outputFormat: "line-by-line"
	        });

}

function loading() {
	document.documentElement.scrollTop = 0;
	$('.load-wrapp').show();
}

