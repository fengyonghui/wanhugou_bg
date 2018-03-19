window.usingNamespace = function (a) {
    var ro = window;
    if (!(typeof (a) === "string" && a.length != 0)) {
        return ro;
    }

    var co = ro;
    var nsp = a.split(".");
    for (var i = 0; i < nsp.length; i++) {
        var cp = nsp[i];
        if (!ro[cp]) {
            ro[cp] = {};
        }
        ;
        co = ro = ro[cp];
    }
    ;

    return co;
};

usingNamespace("Base")["Cache"] = {
    Set: function (key, value) {
        $.cookie(key, value);
        window.sessionStorage.setItem(key, value);
    },
    Get: function (key) {
        var value = window.sessionStorage.getItem(key);
        return window.sessionStorage.getItem(key);
    },
    GetCookieSessionStorage: function (key) {
        var value = window.sessionStorage.getItem(key);
        if (value == null || value == "") {
            value = $.cookie(key)
        }
        if (!value) {
            return window.sessionStorage.getItem(key);
        }
        return value;
    },
    Remove: function (key) {
        $.cookie(key, "");
        window.sessionStorage.removeItem(key);
    }
};
var $CacheUtil = Base.Cache;

usingNamespace("Base")["Date"] = {
    CompareDate: function (d1, d2) {
        return ((new Date(d1.replace(/-/g, "\/"))) > (new Date(d2.replace(/-/g, "\/"))));
    }
};
var $DateUtil = Base.Date;


usingNamespace("Base")["Url"] = {
    getUrlParam: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    },
    getUrlParamChinese: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return decodeURI(r[2]);
        return null;
    },
    go2Url : function (url) {
        window.location.href=url;
    }
};
var $Url = Base.Url;

usingNamespace("Base")["Echatrts"] = {
    showLoadingStyle: {
        type: 'default',
        text: '正在加载...',
        color: '#239df5',
        textColor: '#000000',
        maskColor: 'rgba(255, 255, 255, 0)'
    }
};
var $Echarts = Base.Echatrts;
