/**
 * Tutorial
 * This JavaScript file is for Canvas view.
 */

var tabs = null;

function init() {
    // TODO: Write the code for initializing.
    tabs = new gadgets.TabSet("tutorial_tab");

	/*
	 * Hello World!
	 */
	var helloWorldId = tabs.addTab("Hello World");
	init_hello_world(helloWorldId);
	
	/*
	 * Listing friends
	 */
	var listingFriendId = tabs.addTab("Listing Friends");
	init_listing_friend(listingFriendId);
	
	/*
	 * Giving gift
	 */
	var givingGiftId = tabs.addTab("Giving Gift");
	init_giving_gift(givingGiftId);
	
//	gadgets.window.adjustHeight();
}

// TODO: Write the code for Canvas view.
