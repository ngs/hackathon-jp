var owner_name;
var title;
var data;


function onOwnerIsNotViewer(param) {

	var owner = param.owner;
	var viewer = param.viewer;
//	console.log("aaa" + owner);

	owner_name = owner.getDisplayName();

	title = param.data["title"];
	data = param.data["data"];

//	console.log("bbb" + title);


	var req = opensocial.newDataRequest();
	req.send(function(res) {
		gadgets.window.adjustHeight();
	});
}
