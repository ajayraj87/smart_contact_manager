console.log("java script is running");


const toggleSidebar = () => {
	if ($(".sidebar").is(":visible")) {
		// true
		// sidebar band karna h
		$(".sidebar").css("display", "none");
		$(".contect").css("margin-left", "0.5%");
	} else {
		console.log("Sidebar is not visible");
		// false
		// sidebar ko band nhi karna h
		$(".sidebar").css("display", "block");
		$(".contect").css("margin-left", "20%");
	}
};


// This is OnClick function to delete Contact

function deleteContact(cid) {
	swal({
		title: "Are you sure?",
		text: "you want to delete this contact...",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((willDelete) => {
			if (willDelete) {
				window.location = "/user/delete_contact/" + cid;
			} else {
				swal("Your contact is safe!");
			}
		});
}

// This is Update Contact function

function updateContact(cid) {
	swal({
		title: "Are you sure to update?",
		text: "you want to update this contact...",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((willDelete) => {
			if (willDelete) {
				window.location = "/user/update_contact/" + cid;
			} else {
				swal("Your contact is safe!");
			}
		});
}

// This is Delete User function

function deleteUser() {
	swal({
		title: "Are you sure?",
		text: "you want to delete this contact...",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((willDelete) => {
			if (willDelete) {
				window.location = "/user/delete_user";
			} else {
				swal("Your contact is safe!");
			}
		});
}
