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
var sauiServerSidePreviewDataKey    = "$erns_coching_preview_data";

$giCochingApp = function(){};
$giCochingApp.getServerSideSnsAction = function(){
    var val = window[sauiServerSideSnsActionKey];
    return val;
}

$giCochingApp.getServerSideSnsMessage = function(){
    var val = window[sauiServerSideSnsMessageKey];
    return val;
}

$giCochingApp.getServerSideSnsLoginRet = function(){
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

$giCochingApp.getServerSideSnsJoinRet = function(){
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

$giCochingApp.getServerSideAutoAction = function(){
    var val = window[sauiServerSideAutoActionKey];
    return val;
}

$giCochingApp.getServerSideAutoMessage = function(){
    var val = window[sauiServerSideAutoMessageKey];
    return val;
}

$giCochingApp.getServerSideAutoLoginRet = function(){
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

$giCochingApp.getServerSideAutoJoinRet = function(){
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

$giCochingApp.getServerSideAuchAction = function(){
    var val = window[sauiServerSideAuthActionKey];
    return val;
}

$giCochingApp.getServerSideAuthData = function(){
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

$giCochingApp.getServerSidePreviewData = function() {
    var tval = window[sauiServerSidePreviewDataKey];

    if (tval && tval.length > 0) {
        try {
            return JSON.parse(tval);
        } catch (err) {
            console.error(err); 
        }
    }
    return {};
};
