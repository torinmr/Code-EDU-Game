$.ajaxSetup({
    cache : false
});
var rem = {
    // Skeleton function for RPCs
    rpc : function(funcName, callback, args) {
        if(!args) {
            args = {};
        }
        args.rpcName = funcName;
        rem.lockedGame = true;
        $.getJSON('/codeedugame', args, callback);
    },
}