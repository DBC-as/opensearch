// File that contains an implementation of the ModulesInfo class. 

// WARNING: If you break this file, the module system will stop working! 

// jslib depends on and loads this module.
// The binary uses th following functions:
// 
// __ModulesInfo.checkDepAlreadyLoaded
// __ModulesInfo.addPushModule
// __ModulesInfo.popModuleLoadStack
// __ModulesInfo.removeModule
//
// It is also used indirectly by Use.use.js
// 

// Various files in scripts/*.use.js depends on the interface in this file.

// Note, for various more or less obvious reasons, you can *not* use any other modules
// from this file. Mkay?

var __ModulesInfo = function() {
    var that = {};

    // Hash from moduleName to moduleInfo: name, url, symbols, possibly others at a later time.
    var __modules = {};

    // Stack of modules that are currently beeing loaded
    var __moduleLoadStack = [];

/* Debug function
    that.print_buffer = "";
    var myprint = function() {
        for ( var i = 0; i < arguments.length; ++i ) {
            that.print_buffer += arguments[i];
        }
    }
*/
    // This function does two things. First it checks if we are currently loading a module.
    // If that is the case, then it assumes that the module need to use the moduleName, and adds it
    // to the list of uses for the currently loading module.
    // Secondly, it checks if the module has already been loaded and returns true of false
    //! \todo This is almost always called once with the module it self, while we are loading it. I am not sure why?
    that.checkDepAlreadyLoaded = function( moduleName ) {
//        myprint( "checkDepAlreadyLoaded( " + moduleName + ");\n" );
        if ( this.isLoadingModule() ) {
            var current_module = this.currentLoadingModule();
            if ( current_module !== moduleName ) { // Not sure why this happens.
//                myprint( "  isLoadingModule: " + this.currentLoadingModule() + " has uses: " + __modules[ this.currentLoadingModule() ].uses + "\n" );
                if ( -1 == __modules[ this.currentLoadingModule() ].uses.indexOf( moduleName ) ) {
//                    myprint( "    pushing " + this.currentLoadingModule() + " ==> " + moduleName + "\n" );
                    __modules[ this.currentLoadingModule() ].uses.push( moduleName );
                }
            }
        }
        return this.hasModule( moduleName );
    };

    that.hasModule = function( moduleName ) {
        return moduleName in __modules;
    }

    that.getModule = function( moduleName ) {
        if ( ! this.hasModule( moduleName ) ) {
            throw new Error( "getModule: Module '" + moduleName + "' is not registered" );
        }
        return __modules[ moduleName ];
    };

    that.addPushModule = function( moduleName, url ) {
        if ( this.hasModule( moduleName ) ) {
            throw new Error( "addPushModule: Module '" + moduleName + "' is already registered with url: '" + this.getModule( moduleName ).url + "'" );
        }
        __modules[ moduleName ] = { name: moduleName, url: url, symbols : [], uses: [] };
        try {
            __moduleLoadStack.push( moduleName );
        }
        catch ( e ) {
            delete __modules[ moduleName ];
            throw e;
        }
    };

    that.removeModule = function( moduleName ) {
       if ( ! this.hasModule( moduleName ) ) {
            throw new Error( "removeModule: Module '" + moduleName + "' is not registered" );
        }
        delete __modules[ moduleName ];
    };

    that.popModuleLoadStack = function() {
        return __moduleLoadStack.pop();
    };

    that.isLoadingModule = function() {
        return __moduleLoadStack.length !== 0;
    };

    that.currentLoadingModule = function() {
        if ( ! this.isLoadingModule() ) {
            throw new Error( "currentLoadModule: Not currently loading a module" );
        }
        return __moduleLoadStack[ __moduleLoadStack.length - 1 ]; 
    };

    // The symbols array passed, is copied
    that.addSymbols = function( moduleName, symbols ) {
        if ( ! this.hasModule( moduleName ) ) {
            throw new Error( "addSymbols: Module '" + moduleName + "' is not registered" );
        }
        this.getModule( moduleName ).symbols = symbols.map( function(x) { return x; } );
    };
   
    that.moduleNames = function() {
        var res = [];
        for ( var m in __modules ) {
            if ( __modules.hasOwnProperty( m ) ) {
                res.push( __modules[m].name );
            }
        }   
        return res;
    };

    that.setVersion = function( moduleName, version ) {
        this.getModule(moduleName).version = version;
    };

    that.getVersion = function( moduleName ) {
        return this.getModule( moduleName ).version;
    };

    that.addPushModule("ModulesInfo", "the info module itself");
    that.popModuleLoadStack();


    that.__doc__ = <doc type="object"><brief>Internal jslib object to track module loading</brief>
        <description>This object is used by jslib to track modules loaded, etc. Although there is a public interface, you should only use it from modules that are part of jslib, as the interface may change without warning.</description>
</doc>;
 
    return that;
}();
