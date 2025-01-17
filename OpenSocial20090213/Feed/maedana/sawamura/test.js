
gadgets.util.registerOnLoadHandler(init);


function txt(dom,tag){
    return dom.getElementsByTagName(tag)[0].childNodes[0].nodeValue;
}

function show(oj){
//    var html = new Array();
//    html.push("<h1>");
    var dom = oj.data;
//    html.push( txt(dom,"title") );
//    html.push("</h1>");
//    var items = dom.getElementsByTagName("item");
//    $.each(items,function(){
//        html.push("<p>" + txt(this,"title") + "</p>");
//        html.push("<blockquote>" + txt(this,"description") + "</blockquote>");
//    });
//    document.getElementById('friends').innerHTML += html.join('');
    storeBookData(dom);
    getOwnerId();
}

var params = {};
params[gadgets.io.RequestParameters.METHOD] = gadgets.io.MethodType.GET;
params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.DOM;

gadgets.io.makeRequest("http://booklog.jp/users/sawamur/feed/RSS1", show, params );

function init() {
//    loadFriends();
}

//function loadFriends() {
//    var req = opensocial.newDataRequest();
//    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER), 'viewer');
//
//    var viewerFriends = opensocial.newIdSpec({ "userId" : "VIEWER", "groupId" : "FRIENDS" });
//    var opt_params = {};
//    opt_params[opensocial.DataRequest.PeopleRequestFields.MAX] = 100;
//    req.add(req.newFetchPeopleRequest(viewerFriends, opt_params), 'viewerFriends');
//
//    req.send(onLoadFriends);
//}

//function onLoadFriends(data) {
//    var viewer = data.get('viewer').getData();
//    var viewerFriends = data.get('viewerFriends').getData();
//
//    html = new Array();
//    html.push('<ul>');
//    viewerFriends.each(function(person) {
//        if (person.getId()) {
//        html.push('<li>: ' + person.getDisplayName() + "</li>");
//        }
//    });
//    html.push('</ul>');
//    //document.getElementById('friends').innerHTML = html.join('');
//}

function storeBookData(dom) {
    var req = opensocial.newDataRequest();
    req.add(req.newUpdatePersonAppDataRequest(opensocial.IdSpec.PersonId.VIEWER, 'books', books(dom), 'response'));
    req.send(function(data) {
        if (data.hadError()){
            alert(data.getErrorMessage());
        } else {
            var response = data.get('response');
            if (response.hadError()) {
                alert(response.getErrorCode() + ':' : response.getErrorMessage());
            }
        }
    });
}

function getOwnerId(){
    var req = opensocial.newDataRequest();
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.OWNER), 'owner');
    req.send(function(data) {
        var owner = data.get('owner').getData();
        console.dir(owner);
        return owner.getId();
    });
}

//function loadBookData(ownerId) {
//    var req = opensocial.newDataRequest();
//    //req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.OWNER), 'owner');
//    var params = {};
//    params[opensocial.IdSpec.Field.USER_ID] = opensocial.IdSpec.PersonId.OWNER;
//    var idSpec = opensocial.newIdSpec(params);
//    req.add(req.newFetchPersonAppDataRequest(idSpec, ['books']), 'books');
//    req.send(function(data) {
//        console.dir(data.get('books').geData());
//        //data.get('books').geData()['foo'];
//    })
//}
//
//function books(entryDom) {
//    return {foo: 'foo'}
//}

//function bookMap(entriesDom) {
//    $.each(items,function(){
//
//        html.push("<p>" + txt(this,"title") + "</p>");
//        html.push("<blockquote>" + txt(this,"description") + "</blockquote>");
//    });
//}
