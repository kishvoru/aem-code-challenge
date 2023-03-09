(function (window, document, $, $document) {


$(document).ready(function(e) {

   $("form").on("submit", function(event) {
        event.preventDefault();

		var formData = $(this).serialize();
        $.ajax({
            type: "POST",
            url: "/bin/saveUserDetails",
            data: formData,
            success: function(data, status, xhr){
                alert(data);
            },
            error: function(xhr, status, data){
                alert("Error while persisting UserData :: " + data);
            }
        });

    });

	populateSelect();

});

function populateSelect() {

	$.ajax({
		type: "GET",
		url: "/content/dam/anf-code-challenge/exercise-1/countries.json",
		success: function(data, status, xhr) {
			if (status === "success") {
				var countryEle = $("#country");

				$.each(data, function(propName, propVal) {
					countryEle.append("<option value=" + propVal + ">" + propName + "</option>");
				});

			}
		},
		error: function(xhr, status, data) {
			console.log("Error while loading Country Dropdown :: " + data);
		}

	});
}


})(window, document, jQuery, jQuery(document));


