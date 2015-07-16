"use strict";angular.module("posApp",["LocalStorageModule","tmh.dynamicLocale","ui.bootstrap","ngResource","ui.router","ngCookies","pascalprecht.translate","ngCacheBuster","infinite-scroll","angularMoment","ngAnimate","toaster","angular-loading-bar","checklist-model","validation.match","nvd3ChartDirectives"]).run(["$rootScope","$location","$window","$http","$state","$translate","Language","Auth","Principal","ENV","VERSION",function(a,b,c,d,e,f,g,h,i,j,k){a.ENV=j,a.VERSION=k,a.$on("$stateChangeStart",function(b,c,d){a.toState=c,a.toStateParams=d,i.isIdentityResolved()&&h.authorize(),g.getCurrent().then(function(a){f.use(a)})}),a.$on("$stateChangeSuccess",function(b,d,e,g,h){var i="global.title";a.previousStateName=g.name,a.previousStateParams=h,d.data.pageTitle&&(i=d.data.pageTitle),f(i).then(function(a){c.document.title=a})}),a.back=function(){"activate"===a.previousStateName||null===e.get(a.previousStateName)?e.go("home"):e.go(a.previousStateName,a.previousStateParams)}}]).factory("authInterceptor",["$rootScope","$q","$location","localStorageService",function(a,b,c,d){return{request:function(a){a.headers=a.headers||{};var b=d.get("token");return b&&b.expires_at&&b.expires_at>(new Date).getTime()&&(a.headers.Authorization="Bearer "+b.access_token),a}}}]).factory("authExpiredInterceptor",["$rootScope","$q","$injector","localStorageService",function(a,b,c,d){return{responseError:function(a){if(401===a.status&&("invalid_token"==a.data.error||"Unauthorized"==a.data.error)){d.remove("token");var e=c.get("Principal");if(e.isAuthenticated()){var f=c.get("Auth");f.authorize(!0)}}return b.reject(a)}}}]).config(["$stateProvider","$urlRouterProvider","$httpProvider","$locationProvider","$translateProvider","tmhDynamicLocaleProvider","httpRequestInterceptorCacheBusterProvider",function(a,b,c,d,e,f,g){g.setMatchlist([/.*api.*/,/.*protected.*/],!0),b.otherwise("/"),a.state("site",{"abstract":!0,views:{"navbar@":{templateUrl:"scripts/components/navbar/navbar.html",controller:"NavbarController"}},resolve:{authorize:["Auth",function(a){return a.authorize()}],translatePartialLoader:["$translate","$translatePartialLoader",function(a,b){b.addPart("global")}]}}),c.interceptors.push("authExpiredInterceptor"),c.interceptors.push("authInterceptor"),e.useLoader("$translatePartialLoader",{urlTemplate:"i18n/{lang}/{part}.json"}),e.preferredLanguage("vi"),e.useCookieStorage(),e.useSanitizeValueStrategy("escaped"),f.localeLocationPattern("bower_components/angular-i18n/angular-locale_{{locale}}.js"),f.useCookieStorage(),f.storageKey("NG_TRANSLATE_LANG_KEY")}]),angular.module("posApp").constant("ENV","prod").constant("VERSION","0.0.1-SNAPSHOT"),angular.module("posApp").constant("ENV","dev").constant("VERSION","0.0.1-SNAPSHOT"),angular.module("posApp").factory("Auth",["$rootScope","$state","$q","$translate","Principal","AuthServerProvider","Account","Register","Activate","Password","PasswordResetInit","PasswordResetFinish","Tracker",function(a,b,c,d,e,f,g,h,i,j,k,l,m){return{login:function(a,b){var g=b||angular.noop,h=c.defer();return f.login(a).then(function(a){return e.identity(!0).then(function(b){d.use(b.langKey),d.refresh(),m.sendActivity(),h.resolve(a)}),g()})["catch"](function(a){return this.logout(),h.reject(a),g(a)}.bind(this)),h.promise},logout:function(){f.logout(),e.authenticate(null)},authorize:function(c){return e.identity(c).then(function(){var c=e.isAuthenticated();a.toState.data.roles&&a.toState.data.roles.length>0&&!e.isInAnyRole(a.toState.data.roles)&&(c?b.go("accessdenied"):(a.returnToState=a.toState,a.returnToStateParams=a.toStateParams,b.go("login")))})},createAccount:function(a,b){var c=b||angular.noop;return h.save(a,function(){return c(a)},function(a){return this.logout(),c(a)}.bind(this)).$promise},updateAccount:function(a,b){var c=b||angular.noop;return g.save(a,function(){return c(a)},function(a){return c(a)}.bind(this)).$promise},activateAccount:function(a,b){var c=b||angular.noop;return i.get(a,function(a){return c(a)},function(a){return c(a)}.bind(this)).$promise},changePassword:function(a,b){var c=b||angular.noop;return j.save(a,function(){return c()},function(a){return c(a)}).$promise},resetPasswordInit:function(a,b){var c=b||angular.noop;return k.save(a,function(){return c()},function(a){return c(a)}).$promise},resetPasswordFinish:function(a,b,c){var d=c||angular.noop;return l.save(a,b,function(){return d()},function(a){return d(a)}).$promise}}}]),angular.module("posApp").factory("Principal",["$q","Account","Tracker",function(a,b,c){var d,e=!1;return{isIdentityResolved:function(){return angular.isDefined(d)},isAuthenticated:function(){return e},isInRole:function(a){return e&&d&&d.roles?-1!==d.roles.indexOf(a):!1},isInAnyRole:function(a){if(!e||!d.roles)return!1;for(var b=0;b<a.length;b++)if(this.isInRole(a[b]))return!0;return!1},authenticate:function(a){d=a,e=null!==a},identity:function(f){var g=a.defer();return f===!0&&(d=void 0),angular.isDefined(d)?(g.resolve(d),g.promise):(b.get().$promise.then(function(a){d=a.data,e=!0,g.resolve(d),c.connect()})["catch"](function(){d=null,e=!1,g.resolve(d)}),g.promise)}}}]),angular.module("posApp").directive("hasAnyRole",["Principal",function(a){return{restrict:"A",link:function(b,c,d){var e=function(){c.removeClass("hidden")},f=function(){c.addClass("hidden")},g=function(b){var c;b&&e(),c=a.isInAnyRole(h),c?e():f()},h=d.hasAnyRole.replace(/\s+/g,"").split(",");h.length>0&&g(!0)}}}]).directive("hasRole",["Principal",function(a){return{restrict:"A",link:function(b,c,d){var e=function(){c.removeClass("hidden")},f=function(){c.addClass("hidden")},g=function(b){var c;b&&e(),c=a.isInRole(h),c?e():f()},h=d.hasRole.replace(/\s+/g,"");h.length>0&&g(!0)}}}]),angular.module("posApp").factory("Account",["$resource",function(a){return a("api/account",{},{get:{method:"GET",params:{},isArray:!1,interceptor:{response:function(a){return a}}}})}]),angular.module("posApp").factory("Activate",["$resource",function(a){return a("api/activate",{},{get:{method:"GET",params:{},isArray:!1}})}]),angular.module("posApp").factory("Password",["$resource",function(a){return a("api/account/change_password",{},{})}]),angular.module("posApp").factory("PasswordResetInit",["$resource",function(a){return a("api/account/reset_password/init",{},{})}]),angular.module("posApp").factory("PasswordResetFinish",["$resource",function(a){return a("api/account/reset_password/finish",{},{})}]),angular.module("posApp").factory("Register",["$resource",function(a){return a("api/register",{},{})}]),angular.module("posApp").factory("User",["$resource",function(a){return a("api/users/:id",{},{query:{method:"GET",isArray:!0},get:{method:"GET",transformResponse:function(a){return a=angular.fromJson(a)}},update:{method:"PUT"}})}]),angular.module("posApp").filter("characters",function(){return function(a,b,c){if(isNaN(b))return a;if(0>=b)return"";if(a&&a.length>b){if(a=a.substring(0,b),c)for(;" "===a.charAt(a.length-1);)a=a.substr(0,a.length-1);else{var d=a.lastIndexOf(" ");-1!==d&&(a=a.substr(0,d))}return a+"..."}return a}}).filter("words",function(){return function(a,b){if(isNaN(b))return a;if(0>=b)return"";if(a){var c=a.split(/\s+/);c.length>b&&(a=c.slice(0,b).join(" ")+"...")}return a}}),angular.module("posApp").service("Base64",function(){var a="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";this.encode=function(b){for(var c,d,e,f,g,h="",i="",j="",k=0;k<b.length;)c=b.charCodeAt(k++),d=b.charCodeAt(k++),i=b.charCodeAt(k++),e=c>>2,f=(3&c)<<4|d>>4,g=(15&d)<<2|i>>6,j=63&i,isNaN(d)?g=j=64:isNaN(i)&&(j=64),h=h+a.charAt(e)+a.charAt(f)+a.charAt(g)+a.charAt(j),c=d=i="",e=f=g=j="";return h},this.decode=function(b){var c,d,e,f,g,h="",i="",j="",k=0;for(b=b.replace(/[^A-Za-z0-9\+\/\=]/g,"");k<b.length;)e=a.indexOf(b.charAt(k++)),f=a.indexOf(b.charAt(k++)),g=a.indexOf(b.charAt(k++)),j=a.indexOf(b.charAt(k++)),c=e<<2|f>>4,d=(15&f)<<4|g>>2,i=(3&g)<<6|j,h+=String.fromCharCode(c),64!==g&&(h+=String.fromCharCode(d)),64!==j&&(h+=String.fromCharCode(i)),c=d=i="",e=f=g=j=""}}).factory("StorageService",["$window",function(a){return{get:function(b){return JSON.parse(a.localStorage.getItem(b))},save:function(b,c){a.localStorage.setItem(b,JSON.stringify(c))},remove:function(b){a.localStorage.removeItem(b)},clearAll:function(){a.localStorage.clear()}}}]),angular.module("posApp").service("ParseLinks",function(){this.parse=function(a){if(0==a.length)throw new Error("input must not be of zero length");var b=a.split(","),c={};return angular.forEach(b,function(a){var b=a.split(";");if(2!=b.length)throw new Error("section could not be split on ';'");var d=b[0].replace(/<(.*)>/,"$1").trim(),e={};d.replace(new RegExp("([^?=&]+)(=([^&]*))?","g"),function(a,b,c,d){e[b]=d});var f=e.page;angular.isString(f)&&(f=parseInt(f));var g=b[1].replace(/rel="(.*)"/,"$1").trim();c[g]=f}),c}}),angular.module("posApp").service("DateUtils",function(){this.convertLocaleDateToServer=function(a){if(a){var b=new Date;return b.setUTCDate(a.getDate()),b.setUTCMonth(a.getMonth()),b.setUTCFullYear(a.getFullYear()),b}return null},this.convertLocaleDateFromServer=function(a){if(a){var b=a.split("-");return new Date(b[0],b[1]-1,b[2])}return null},this.convertDateTimeFromServer=function(a){return a?new Date(a):null}}),angular.module("posApp").config(["$stateProvider",function(a){a.state("error",{parent:"site",url:"/error",data:{roles:[],pageTitle:"error.title"},views:{"content@":{templateUrl:"scripts/app/error/error.html"}},resolve:{mainTranslatePartialLoader:["$translate","$translatePartialLoader",function(a,b){return b.addPart("error"),a.refresh()}]}}).state("accessdenied",{parent:"site",url:"/accessdenied",data:{roles:[]},views:{"content@":{templateUrl:"scripts/app/error/accessdenied.html"}},resolve:{mainTranslatePartialLoader:["$translate","$translatePartialLoader",function(a,b){return b.addPart("error"),a.refresh()}]}})}]),angular.module("posApp").factory("Language",["$q","$http","$translate","LANGUAGES",function(a,b,c,d){return{getCurrent:function(){var b=a.defer(),d=c.storage().get("NG_TRANSLATE_LANG_KEY");return angular.isUndefined(d)&&(d="vi"),b.resolve(d),b.promise},getAll:function(){var b=a.defer();return b.resolve(d),b.promise}}}]).constant("LANGUAGES",["vi","en","fr"]),angular.module("posApp").controller("LanguageController",["$scope","$translate","Language","tmhDynamicLocale",function(a,b,c,d){a.changeLanguage=function(a){b.use(a),d.set(a)},c.getAll().then(function(b){a.languages=b})}]).filter("findLanguageFromKey",function(){return function(a){return{en:"English",fr:"Français",de:"Deutsch",it:"Italiano",ru:"Русский",tr:"Türkçe",ca:"Català",da:"Dansk",es:"Español",hu:"Magyar",ja:"日本語",kr:"한국어",pl:"Polski","pt-br":"Português (Brasil)",ro:"Română",sv:"Svenska","zh-cn":"中文（简体）","zh-tw":"繁體中文",vi:"Việt Nam"}[a]}}),angular.module("posApp").factory("AuthServerProvider",["$http","localStorageService","Base64",function(a,b,c){return{login:function(d){var e="username="+d.username+"&password="+d.password+"&grant_type=password&scope=read%20write&client_secret=mySecretOAuthSecret&client_id=posapp";return a.post("oauth/token",e,{headers:{"Content-Type":"application/x-www-form-urlencoded",Accept:"application/json",Authorization:"Basic "+c.encode("posapp:mySecretOAuthSecret")}}).success(function(a){var c=new Date;return c.setSeconds(c.getSeconds()+a.expires_in),a.expires_at=c.getTime(),b.set("token",a),a})},logout:function(){a.post("api/logout").then(function(){b.clearAll()})},getToken:function(){return b.get("token")},hasValidToken:function(){var a=this.getToken();return a&&a.expires_at&&a.expires_at>(new Date).getTime()}}}]),angular.module("posApp").config(["$stateProvider",function(a){a.state("tracker",{parent:"admin",url:"/tracker",data:{roles:["ROLE_ADMIN"],pageTitle:"tracker.title"},views:{"content@":{templateUrl:"scripts/app/admin/tracker/tracker.html",controller:"TrackerController"}},resolve:{mainTranslatePartialLoader:["$translate","$translatePartialLoader",function(a,b){return b.addPart("tracker"),a.refresh()}]},onEnter:["Tracker",function(a){a.subscribe()}],onExit:["Tracker",function(a){a.unsubscribe()}]})}]),angular.module("posApp").controller("TrackerController",["$scope","AuthServerProvider","$cookies","$http","Tracker",function(a,b,c,d,e){function f(b){for(var c=!1,d=0;d<a.activities.length;d++)a.activities[d].sessionId==b.sessionId&&(c=!0,"logout"==b.page?a.activities.splice(d,1):a.activities[d]=b);c||"logout"==b.page||a.activities.push(b)}a.activities=[],e.receive().then(null,null,function(a){f(a)})}]),angular.module("posApp").factory("Tracker",["$rootScope","$cookies","$http","$q",function(a,b,c,d){function e(){null!=f&&f.connected&&f.send("/topic/activity",{},JSON.stringify({page:a.toState.name}))}var f=null,g=null,h=d.defer(),i=d.defer(),j=!1;return{connect:function(){var d=window.location,g="//"+d.host+d.pathname+"websocket/tracker",h=new SockJS(g);f=Stomp.over(h);var k={};k["X-CSRF-TOKEN"]=b[c.defaults.xsrfCookieName],f.connect(k,function(b){i.resolve("success"),e(),j||(a.$on("$stateChangeStart",function(a){e()}),j=!0)})},subscribe:function(){i.promise.then(function(){g=f.subscribe("/topic/tracker",function(a){h.notify(JSON.parse(a.body))})},null,null)},unsubscribe:function(){null!=g&&g.unsubscribe()},receive:function(){return h.promise},sendActivity:function(){null!=f&&e()},disconnect:function(){null!=f&&(f.disconnect(),f=null)}}}]),angular.module("posApp").factory("Utils",[function(){var a={isUndefinedOrNull:function(a){return angular.isUndefined(a)||null===a},isInt:function(a){return Number(a)===a&&a%1===0},isFloat:function(a){return a===Number(a)&&a%1!==0}};return a}]).constant("Constants",{orderStatus:{cancel:"CANCEL",hold:"HOLD",payment:"PAYMENT"},dateTimePattern:"DD/MM/YYYY HH:mm:ss",fractionSize:2,perPage:6,all:"ALL"}),angular.module("posApp").run(["$rootScope","Constants",function(a,b){a.constants=b}]),angular.module("posApp").directive("numberFormat",["$filter","Utils","Constants",function(a,b,c){return{restrict:"A",link:function(d,e,f){d.$watch(f.numberFormat,function(d){!b.isUndefinedOrNull(d)&&angular.isNumber(d)&&(b.isInt(d)?e.text(a("number")(d,0)):b.isFloat(d)&&e.text(a("number")(d,c.fractionSize)))})}}}]).directive("userAuthority",["$filter","Utils","Constants",function(a,b,c){return{restrict:"A",link:function(a,c,d){a.$watch(d.userAuthority,function(a){if(!b.isUndefinedOrNull(a)&&a.length>0){var d=[];angular.forEach(a,function(a){d.push(a.name)}),d.length>0&&c.text(d.join(","))}})}}}]),angular.module("posApp").factory("OrderService",["$http",function(a){return{getByStatus:function(b,c,d,e,f){return a.get("api/orders",{params:{page:b,per_page:c,status:d,from:e,to:f}})},getSumAmountByStatusCreatedDate:function(b,c,d){return a.get("api/orders/amount",{params:{status:b,from:c,to:d}})},getById:function(b){var c="api/orders/"+b;return a.get(c)},createOrder:function(b){return a.post("/api/orders",b)},moveItem:function(b){return a.post("/api/orders/move",b)}}}]),angular.module("posApp").config(["$stateProvider",function(a){a.state("print",{parent:"site",url:"/print/{id}",data:{roles:[],pageTitle:"print.title"},views:{"content@":{templateUrl:"scripts/app/print/print.html",controller:"PrintController"}},resolve:{mainTranslatePartialLoader:["$translate","$translatePartialLoader",function(a,b){return b.addPart("common"),b.addPart("order"),b.addPart("print"),a.refresh()}]}})}]),angular.module("posApp").controller("PrintController",["$scope","$filter","$stateParams","$window","$timeout","toaster","Principal","Constants","Utils","OrderService",function(a,b,c,d,e,f,g,h,i,j){a.order={},a.load=function(b){j.getById(b).then(function(b){i.isUndefinedOrNull(b)||(a.order=b.data,e(function(){d.print()},3e3))})},a.load(c.id)}]);