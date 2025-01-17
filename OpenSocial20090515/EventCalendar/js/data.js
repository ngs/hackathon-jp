//alert("test");
var evtCal = {
	dao: {},
	
	// 定数
	// イベントのステータス
	status: {
		CREATING: "creating",
		FIX: "fix",
		CANCEL: "cancel"
	},
	// エントリの種類
	entryType: {
		WHEN: "いつ",
		WHERE: "どこで",
		WHAT: "何を",
		WHO: "誰と"		
	},
	
	// 以下、ファクトリーメソッド
	
	createUser: function(id, nickname){
		var user = {
			id: id,
			nickname: nickname
		};
		return user;
	},
	createEntry: function(type, user, postedAt, content, comment){
		var entry = {
			id: evtCal.dao.__private.getNextEntryId(),
			type: type,
			user: user,
			postedAt: postedAt,
			content: content,
			comment: comment
		};
		return entry;
	},
	createEvent: function(title, creator, participants, status){
		var event = {
			id: evtCal.dao.__private.getNextEventId(),
			title: title,
			creator: creator,
			participants: participants,
			status: status,
			entries: []
		};
		return event;
	}
};

/**
 * DAOのダミー実装
 */
var DummyDao = {
	// __private以下は非公開メソッド
	__private: {
		__lastEntryId: 0,
		__lastEventId: 0,
		getNextEntryId: function(){
			return this.__lastEntryId++;
		},
		getNextEventId: function(){
			return this.__lastEventId++;
		}
	},
	
	getEventsByOwerUserId: function(userId){
		var events = [];
		var user = evtCal.createUser(userId, "sugimori");
		var evt0 = evtCal.createEvent("花見", user, [user], evtCal.status.CRIATING);
		evt0.entries.push(evtCal.createEntry(evtCal.entryType.WHEN, user, new Date(), "5/15 10:00 から", "遅れないように"));
		events.push(evt0);
		events.push(evtCal.createEvent("Hackathon", user, [user], evtCal.status.CRIATING));
		return events;
	},
	
	getEventById: function(eventId){
		var events = this.getEventsByOwerUserId("test");
		return (eventId == 1) ? events[1] : events[0];
	},
	
	addEntry: function(eventId, entry){
	},
	
	updateEvent: function(event){
	},
	
	registerEvent: function(event){
	}
};

var CouchRequester = {
	findAllEvents: function(url){
		gadgets.io.makeRequest(url, function(obj){
			obj;
		});
	}
};

evtCal.dao = DummyDao;