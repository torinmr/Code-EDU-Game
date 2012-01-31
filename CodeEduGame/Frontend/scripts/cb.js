var cb = {
    callbacks : {},
    add : function(n, f) {
        cb.callbacks[n] = f;
    },
    call : function(n, a) {
        if(cb.callbacks[n]) {
        	if (typeof a != 'undefined') {
        		cb.callbacks[n](a);
        	} else {
        		cb.callbacks[n]();
        	}
        }
    },
    del : function(n) {
        delete cb.callbacks[n];
    },
    clear : function() {
    	cb.callbacks = {};
    }
};
