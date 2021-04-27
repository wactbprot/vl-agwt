function(doc) {
    var req_id = null;
    req_val = {} 
    // Planning
    if (doc.Planning){
	if (doc.Planning.RequestId) {
	    req_id = doc.Planning.RequestId;
 	}
	req_val["Planning"] = true;
    }
    
    // Calibration
    if(doc.Calibration) {
	if ( doc.Calibration.Presettings && doc.Calibration.Presettings.RequestId) {
	    req_id = doc.Calibration.Presettings.RequestId;
	    req_val["Calibration"] = true;

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
    }
    
    // Bureaucracy
    if (doc.Bureaucracy){
	if ( doc.Bureaucracy.RequestId) {
	    req_id = doc.Bureaucracy.RequestId;
	    req_val["Bureaucracy"] = true;
	    req_val["Calibrations"] = doc.Bureaucracy.Certificate.length;
	}
    }
    
    // Certificate
    if(doc.Certificate){
	if(doc.Certificate.RequestId){
	    req_id = doc.Certificate.RequestId;
	    
	    req_val["Certificate"] = true;
	    
	}
    }
    
    // DCC
    if(doc.DCC){
	if(doc.DCC.RequestId){
	    req_id = doc.DCC.RequestId;
	    req_val["DCC"] = true;
	}
	
    }
    
    if(req_id){
	emit(req_id, req_val);
    }
}
