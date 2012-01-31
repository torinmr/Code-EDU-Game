var cb = {
    callbacks : {},
    add : function(n, f) {
        cb.callbacks[n] = f;
    },
    call : function(n) {
        if(cb.callbacks[n]) {
            cb.callbacks[n]();
        }
    },
    del : function(n) {
        delete cb.callbacks[n];
    },
    clear : function() {
    	cb.callbacks = {};
    }
};
