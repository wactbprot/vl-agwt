function(doc) {
    var req_id = null;
    req_val = {} 
    // Planning
    if (doc.Planning){
	if (doc.Planning.RequestId) {
	    req_id = doc.Planning.RequestId;
	}else{
	    req_id = doc._id;
	}
	req_val["Planning"] = true;
    }
   
    // Calibration
    if(doc.Calibration) {
	if ( doc.Calibration.Presettings && doc.Calibration.Presettings.RequestId) {
	    req_id = doc.Calibration.Presettings.RequestId;
	}else{
	    req_id = doc.Calibration.Presettings.CommonReferenceNo;
	}
	
	if(doc.Calibration.Measurement){
	    if(doc.Calibration.Measurement.Values ){
		req_val["Measurement"] = true;
	    }
	}
	if(doc.Calibration.Analysis){
	    if(doc.Calibration.Analysis.Values ){
		req_val["Analysis"] = true;
	    }
	}
	if(doc.Calibration.Result){
	    if(doc.Calibration.Result.Date ){
		req_val["Result"] = true;
	    }
	}
	    
    }
    // Bureaucracy
    if (doc.Bureaucracy){
	if ( doc.Bureaucracy.RequestId) {
	    req_id = doc.Bureaucracy.RequestId;
	}else{
	    req_id = doc.Bureaucracy.CommonReferenceNo;
	}
	req_val["Bureaucracy"] = true;
	req_val["Calibrations"] = doc.Bureaucracy.Certificate.length;
    }
    
    // Certificate
    if(doc.Certificate){
	if(doc.Certificate.RequestId){
	   req_id = doc.Certificate.RequestId;
	}else{
	    // "7.5-1.5-21-4-12"
	    var special_ref_no = doc.Certificate.Titlepage.ReferenceNo.split("-");
	    // "7.5-1V-21-12"
	    req_id =  [special_ref_no[0],
		       "1V",
		       special_ref_no[2],
		       special_ref_no[4]].join("-");
	}
	req_val["Certificate"] = true;

    }
    
    if(req_id && req_id.substring(0,3) =="7.5" ||   req_id.substring(0,3) =="pla" ){
	emit(req_id, req_val);
    }
}
