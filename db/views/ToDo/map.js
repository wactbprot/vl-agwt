function(doc) {
    
    if(doc.ToDo && doc.ToDo.Standard &&  doc.ToDo.Standard == "SE3"){
	
	var dev = doc.ToDo.DeviceClass,
	    u = doc.ToDo.Values.Pressure.Unit,
	    p = doc.ToDo.Values.Pressure.Value,
	    l = p.length,
	    min = p[0],
	    max = p[l-1];

	emit(dev + " von: " + min + u + " bis: " + max + u + " mit " + l +" Messpunkten", doc);
    }
    
    
}
