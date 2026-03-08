var sauiServerSideSnsActionKey      = "$erns_coching_sns_action";
var sauiServerSideSnsMessageKey     = "$erns_coching_sns_message";
var sauiServerSideSnsLoginRetKey    = "$erns_coching_sns_loginRet";
var sauiServerSideSnsJoinRetKey     = "$erns_coching_sns_joinRet";
var sauiServerSideAutoActionKey      = "$erns_coching_auto_action";
var sauiServerSideAutoMessageKey     = "$erns_coching_auto_message";
var sauiServerSideAutoLoginRetKey    = "$erns_coching_auto_loginRet";
var sauiServerSideAutoJoinRetKey     = "$erns_coching_auto_joinRet";
var sauiServerSideAuthActionKey     = "$erns_coching_auth_action";
var sauiServerSideAuthDataKey       = "$erns_coching_auth_data";

$giCochingPartner = function(){};
$giCochingPartner.getServerSideSnsAction = function(){
    var val = window[sauiServerSideSnsActionKey];
    return val;
}

$giCochingPartner.getServerSideSnsMessage = function(){
    var val = window[sauiServerSideSnsMessageKey];
    return val;
}

$giCochingPartner.getServerSideSnsLoginRet = function(){
    var tval = window[sauiServerSideSnsLoginRetKey];
    if(tval && tval.length > 0){
        try{
            return JSON.parse(tval);
        }catch(err){
            console.error(err);            
        }
    }
    return {};
}

$giCochingPartner.getServerSideSnsJoinRet = function(){
    var tval = window[sauiServerSideSnsJoinRetKey];
    if(tval && tval.length > 0){
        try{
            return JSON.parse(tval);
        }catch(err){
            console.error(err);    
        }
    }
    return {};
}

$giCochingPartner.getServerSideAutoAction = function(){
    var val = window[sauiServerSideAutoActionKey];
    return val;
}

$giCochingPartner.getServerSideAutoMessage = function(){
    var val = window[sauiServerSideAutoMessageKey];
    return val;
}

$giCochingPartner.getServerSideAutoLoginRet = function(){
    var tval = window[sauiServerSideAutoLoginRetKey];
    if(tval && tval.length > 0){
        try{
            return JSON.parse(tval);
        }catch(err){
            console.error(err);            
        }
    }
    return {};
}

$giCochingPartner.getServerSideAutoJoinRet = function(){
    var tval = window[sauiServerSideAutoJoinRetKey];
    if(tval && tval.length > 0){
        try{
            return JSON.parse(tval);
        }catch(err){
            console.error(err);    
        }
    }
    return {};
}

$giCochingPartner.getServerSideAuthAction = function(){
    var val = window[sauiServerSideAuthActionKey];
    return val;
}

$giCochingPartner.getServerSideAuthData = function(){
    var tval = window[sauiServerSideAuthDataKey];
    if(tval && tval.length > 0){
        try{
            return JSON.parse(tval);
        }catch(err){
            console.error(err);    
        }
    }
    return {};
}


