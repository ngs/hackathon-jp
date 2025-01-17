var owner = null;
var FLASH_ID = "GenerateSound";

var getViewer = function() {
  var os = opensocial.Container.get();
  var dataRequest = os.newDataRequest();
  var param = {};
  param[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = [opensocial.Person.Field.ID,MyOpenSpace.Person.Field.ABOUT,MyOpenSpace.Person.Field.BOOKS];
  var viewerReq = os.newFetchPersonRequest(opensocial.DataRequest.PersonId.VIEWER, param);
  var soundCollectionReq = os.newFetchPersonAppDataRequest(
    opensocial.DataRequest.PersonId.VIEWER,
    ["soundCollection"]);

  dataRequest.add(viewerReq);
  dataRequest.add(soundCollectionReq, "soundCollection");
  dataRequest.send(getResponse);

  function getResponse(response) {
    var flashobj = document.getElementById(FLASH_ID);
    var res = {};
    var viewerData = {};
    if (response.hadError()) {
      res = {success:false, message:"getViewer error.\n" + response .getErrorCode() + "\n"+ response .getErrorMessage()};
    } else {
      var viewer = response.get(opensocial.DataRequest.PersonId.VIEWER).getData();
      var soundCollections = response.get("soundCollection").getData();
      var hasOwnerSound = false;
      if (soundCollections) {
        console.log(soundCollections);
        var sc = soundCollections[viewer.getId()];
        var soundCollection = gadgets.json.parse(sc["soundCollection"]);
        if (soundCollection) {
          for (var i = 0; i < soundCollection.length; i++) {
            if (soundCollection[i].id == owner.getId()) {
              console.log(soundCollection[i].id);
              hasOwnerSound = true;
              break;
            }
          }
        }
        console.log(soundCollection);
      }
      viewerData = {
        'id' : viewer.getField(opensocial.Person.Field.ID),
        'name' : viewer.getField(opensocial.Person.Field.NAME),
        'thumbnailUrl' : viewer.getField(opensocial.Person.Field.THUMBNAIL_URL),
        'sound' : null,
        'hasOwnerSound' : hasOwnerSound,
      }
      res = {success:true, message:""};
    }
    flashobj.setViewer({viewer:viewerData, result:res});
  }
}

var getOwner = function() {
  var os = opensocial.Container.get();
  var dataRequest = os.newDataRequest();
  var dr = opensocial.DataRequest;
  var param = {};
  param[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = [opensocial.Person.Field.ID,MyOpenSpace.Person.Field.ABOUT,MyOpenSpace.Person.Field.BOOKS];
  var ownerReq = os.newFetchPersonRequest(opensocial.DataRequest.PersonId.OWNER, param);
  //var escapeParams = {};
  //escapeParams [opensocial.DataRequest.DataRequestFields.ESCAPE_TYPE] = opensocial.EscapeType.NONE;
  var soundParamReq = os.newFetchPersonAppDataRequest(
    opensocial.DataRequest.PersonId.OWNER,
    ["soundParam"]);
//    escapeParams);

  dataRequest.add(ownerReq, "owner");
  dataRequest.add(soundParamReq, "soundParam");

  dataRequest.send(getResponse);

  function getResponse(response) {
    var flashobj = document.getElementById(FLASH_ID);
    var res = {};
    var ownerData = {};
    if (response.hadError()) {
      res = {success:false, message:"getViewer error.\n" + response .getErrorCode() + "\n"+ response .getErrorMessage()};
    } else {
      owner = response.get("owner").getData();
      var soundParams = response.get("soundParam").getData();
      var soundParam = null;
      if (soundParams) {
        console.log(soundParams);
        var sp = soundParams[owner.getId()];
        soundParam = gadgets.json.parse(sp["soundParam"]);
        console.log(soundParam);
      }
      var ownerData = {
        'id' : owner.getField(opensocial.Person.Field.ID),
        'name' : owner.getField(opensocial.Person.Field.NAME),
        'city' : owner.getField(MyOpenSpace.Person.Field.CITY),
        'region' : owner.getField(MyOpenSpace.Person.Field.REGION),
        'postalcode' : owner.getField(MyOpenSpace.Person.Field.POSTALCODE),
        'country' : owner.getField(MyOpenSpace.Person.Field.COUNTRY),
        'hometown' : owner.getField(MyOpenSpace.Person.Field.HOMETOWN),
        'age' : owner.getField(MyOpenSpace.Person.Field.AGE),
        'gender' : owner.getField(MyOpenSpace.Person.Field.GENDER),
        'culture' : owner.getField(MyOpenSpace.Person.Field.CULTURE),
        'about' : owner.getField(MyOpenSpace.Person.Field.ABOUT),
        'maritalStatus' : owner.getField(MyOpenSpace.Person.Field.MARITAL_STATUS),
        'headline' : owner.getField(MyOpenSpace.Person.Field.HEADLINE),
        'occupation' : owner.getField(MyOpenSpace.Person.Field.OCCUPATION),
        'desireToMeet' : owner.getField(MyOpenSpace.Person.Field.DESIRE_TO_MEET),
        'interests' : owner.getField(MyOpenSpace.Person.Field.INTERESTS),
        'music' : owner.getField(MyOpenSpace.Person.Field.MUSIC),
        'movies' : owner.getField(MyOpenSpace.Person.Field.MOVIES),
        'television' : owner.getField(MyOpenSpace.Person.Field.TELEVISION),
        'books' : owner.getField(MyOpenSpace.Person.Field.BOOKS),
        'heroes' : owner.getField(MyOpenSpace.Person.Field.HEROES),
        'zodiacSign' : owner.getField(MyOpenSpace.Person.Field.ZODIAC_SIGN),
        'mood' : owner.getField(MyOpenSpace.Person.Field.MOOD),
        'status' : owner.getField(MyOpenSpace.Person.Field.STATUS),
        'thumbnailUrl' : owner.getField(opensocial.Person.Field.THUMBNAIL_URL),
        'sound' : soundParam,
      }
      res = {success:true, message:""};
    }
    flashobj.setOwner({owner:ownerData, result:res});
  }
}

var saveSound = function(param) {
  var os = opensocial.Container.get();
  var o = opensocial;
  var res = {};
  var dataRequest = os.newDataRequest();
  var flashobj = document.getElementById(FLASH_ID);
  if (param) {
    var json = gadgets.json.stringify(param);
    console.log(json);
    var soundParamReq = dataRequest .newUpdatePersonAppDataRequest(
      opensocial.DataRequest.PersonId.VIEWER,
      "soundParam",
      json);
    dataRequest.add(soundParamReq, "saveSoundParamResult");
    dataRequest.send(onSaveSoundComplete);
  } else {
    res = {success:false, message:"saveSound error. invalid param."};
    console.log(res);
    flashobj.setSaveSoundResult({result:res});
  }

  function onSaveSoundComplete(response) {
    var res = {};
    //if (response.hadError()) {
    //  res = {success:false, message:"data request error.\n" + response.getErrorCode() + "\n"+ response.getErrorMessage()};
    //} else {
      var result = response.get("saveSoundParamResult");
      if (result .hadError()) {
        console.log(result);
        res = {success:false, message:"savSound error.\n" + result .getErrorCode() + "\n"+ result .getErrorMessage()};
      } else {
        console.log(result);
        res = {success:true, message:""};
      }
    //}
    flashobj.setSaveSoundResult({result:res});
  }
}

var getSoundCollection = function() {
  var os = opensocial.Container.get();
  var dataRequest = os.newDataRequest();
  var flashobj = document.getElementById(FLASH_ID);
  var res = {};
  var soundCollectionReq = os.newFetchPersonAppDataRequest(
    opensocial.DataRequest.PersonId.OWNER,
    ["soundCollection"]);
    dataRequest.add(soundCollectionReq, "soundCollection");
    dataRequest.send(onSoundCollectionComplete);

  function onSoundCollectionComplete(response) {
    var res = {success:true, message:""};
    var soundCollection = [];
    if (response.hadError()) {
      res = {success:false, message:"getSoundCollection error.(get)\n" + response .getErrorCode() + "\n"+ response .getErrorMessage()};
      flashobj.setGetSoundCollectionResult({sounds:[], result:res});
    } else {
      var soundCollections = response.get("soundCollection").getData();
      if (soundCollections) {
        console.log(soundCollections);
        var sc = soundCollections[owner.getId()];
        if (sc) {
          soundCollection = gadgets.json.parse(sc["soundCollection"]);
          console.log(soundCollection);
        }
      }
      flashobj.setGetSoundCollectionResult({sounds:soundCollection, result:res});
    }
  }
}

var addSound = function(ownerId, viewerId) {
  var os = opensocial.Container.get();
  var dataRequest = os.newDataRequest();
  var flashobj = document.getElementById(FLASH_ID);
  var res = {};
  if (ownerId && viewerId) {
    var viewerReq = os.newFetchPersonRequest(opensocial.DataRequest.PersonId.VIEWER);
    var soundCollectionReq = os.newFetchPersonAppDataRequest(
      opensocial.DataRequest.PersonId.VIEWER,
      ["soundCollection"]);
    dataRequest.add(viewerReq);
    dataRequest.add(soundCollectionReq, "soundCollection");
    dataRequest.send(onSoundCollectionComplete);
  } else {
    res = {success:false, message:"addSound error. invalid param."};
    console.log(res);
    flashobj.setAddSoundResult({result:res});
  }

  function onSoundCollectionComplete(response) {
    var res = {};
    if (response.hadError()) {
      res = {success:false, message:"addSound error.(get)\n" + response .getErrorCode() + "\n"+ response .getErrorMessage()};
      flashobj.setAddSoundResult({result:res});
    } else {
      var viewer = response.get(opensocial.DataRequest.PersonId.VIEWER).getData();
      var soundCollections = response.get("soundCollection").getData();
      var newSoundCollection = [];
      if (!viewer) {
        res = {success:false, message:"addSound error.(get)\nviewer not found."};
        flashobj.setAddSoundResult({result:res});
      } else {
        if (soundCollections) {
          console.log(soundCollections);
          var sc = soundCollections[viewer.getId()];
          var soundCollection = gadgets.json.parse(sc["soundCollection"]);
          if (soundCollection) {
            newSoundCollection = soundCollection;
          }
          console.log(soundCollection);
        }
        newSoundCollection[newSoundCollection.length] = viewerId;
        var json = gadgets.json.stringify(newSoundCollection);
        console.log(json);
        dataRequest = os.newDataRequest();
        var updateSoundCollectionReq = dataRequest .newUpdatePersonAppDataRequest(
          opensocial.DataRequest.PersonId.VIEWER,
          "soundCollection",
          json);
        dataRequest.add(updateSoundCollectionReq);
        dataRequest.send(onUpdateSoundCollectionComplete);
      }
    }
  }

  function onUpdateSoundCollectionComplete(response) {
    res = {};
    if (response .hadError()) {
      console.log(response);
      res = {success:false, message:"addSound error.(update)\n" + response .getErrorCode() + "\n"+ response .getErrorMessage()};
    } else {
      console.log(response);
      res = {success:true, message:""};
    }
    flashobj.setAddSoundResult({result:res});
  }
}

var clearSound = function() {
  var os = opensocial.Container.get();
  var dataRequest = os.newDataRequest();
  var res = {};
  var newSoundCollection = [];
  var json = gadgets.json.stringify(newSoundCollection);
  console.log(json);
  dataRequest = os.newDataRequest();
  var clearSoundCollectionReq = dataRequest .newUpdatePersonAppDataRequest(
    opensocial.DataRequest.PersonId.VIEWER,
    "soundCollection",
     json);
  dataRequest.add(clearSoundCollectionReq);
  dataRequest.send(onClearSoundCollectionComplete);

  function onClearSoundCollectionComplete(response) {
    if (response .hadError()) {
      console.log(response);
      res = {success:false, message:"clearSound error.\n" + response .getErrorCode() + "\n"+ response .getErrorMessage()};
    } else {
      console.log(response);
      res = {success:true, message:"clearSound success."};
    }
    var flashobj = document.getElementById(FLASH_ID);
    flashobj.setClearSoundResult({result:res});
  }
}

var test = function() {
  var flashobj = document.getElementById(FLASH_ID);
  var ownerData = {
    'id' : "1123",
    'name' : "Test User",
    'age' : "34",
    'gender' : "Male",
    'thumbnailUrl' : "http://x.myspacecdn.com/images/no_pic.gif",
    'soundParam' : "xxxxxxxxxxxx",
  }
  var res = {success:true, message:""};
  flashobj.setOwner({owner:ownerData, result:res});
}

var init = function() {}
init();
