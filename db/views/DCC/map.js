function(doc) {
    
    if(doc.Certificate && doc.Certificate.RequestId && doc._attachments){
	
	for (a in doc._attachments) {
	    if(a.substr(0,3) == "dcc"){
		emit(doc.Certificate.RequestId, a);
	    }
	}
    }
    
}
