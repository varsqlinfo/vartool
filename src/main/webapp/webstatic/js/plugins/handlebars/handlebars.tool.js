/**
handlebars custom function 
 */

if (typeof window != "undefined") {
    if (typeof window.VARTOOLTemplate == "undefined") {
        window.VARTOOLTemplate = {};
    }
}else{
	if(!VARTOOLTemplate){
		VARTOOLTemplate = {};
	}
}


;(function(Handlebars, VARTOOL) {
	"use strict";

	
Handlebars.registerHelper("camelCase", function(text, options) {
    return VARTOOL.util.convertCamel(text);
});

Handlebars.registerHelper("upperCase", function(text, options) {
    return VARTOOL.util.toUpperCase(text);
});

Handlebars.registerHelper("lowerCase", function(text, options) {
    return VARTOOL.util.capitalize(text);
});

Handlebars.registerHelper("capitalize", function(text, options) {
    return  VARTOOL.util.capitalize(text);;
});

Handlebars.registerHelper('xif', function (v1,o1,v2,mainOperator,v3,o2,v4,options) {
    var operators = {
         '==': function(a, b){ return a==b},
         '===': function(a, b){ return a===b},
         '!=': function(a, b){ return a!=b},
         '!==': function(a, b){ return a!==b},
         '<': function(a, b){ return a<b},
         '<=': function(a, b){ return a<=b},
         '>': function(a, b){ return a>b},
         '>=': function(a, b){ return a>=b},
         '&&': function(a, b){ return a&&b},
         '||': function(a, b){ return a||b},
      }
    var a1 = operators[o1](v1,v2);
    var a2 = operators[o2](v3,v4);
    var isTrue = operators[mainOperator](a1, a2);
    return isTrue ? options.fn(this) : options.inverse(this);
});


VARTOOLTemplate.parse = function (template, errorHandler){
	try{
		return Handlebars.parse(template); 
    }catch(e){
    	if(errorHandler){
    		errorHandler(e);
    	}
    	return false; 
    }
}

VARTOOLTemplate.compile = function (template ,errorHandler){
	try{
		return Handlebars.compile(template); 
    }catch(e){
    	if(errorHandler){
    		errorHandler(e);
    	}
    	return false; 
    }
	
}

VARTOOLTemplate.render ={
	/**
	 * @method html
	 * @description html template render
	 */
	html : function (template, item, errorHandler){
		try{
			var template = Handlebars.compile(template);
	        return template(item);
        }catch(e){
        	if(errorHandler){
        		errorHandler(e);
        	}
        	return e.message;
        }
	}
	/**
	 * @method text
	 * @description text template render
	 */
	,text : function (template, item, errorHandler){
		try{
			var template = Handlebars.compile(template);
	        return template(item);
        }catch(e){
        	if(errorHandler){
        		errorHandler(e);
        	}
        	return e.message;
        }
	}
}


}(Handlebars, VARTOOL));