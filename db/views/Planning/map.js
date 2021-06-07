function(doc) {
    if(doc.Planning && doc.Planning.RequestId){
	emit(doc.Planning.RequestId, doc);
    }
}
