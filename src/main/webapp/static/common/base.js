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
    go2Url: function (url) {
        window.location.href = url;
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

/*
 * Map对象，实现Map功能
 * size() 获取Map元素个数
 * isEmpty() 判断Map是否为空
 * clear() 删除Map所有元素
 * put(key, value) 向Map中增加元素（key, value)
 * remove(key) 删除指定key的元素，成功返回true，失败返回false
 * get(key) 获取指定key的元素值value，失败返回null
 * element(index) 获取指定索引的元素（使用element.key，element.value获取key和value），失败返回null
 * containsKey(key) 判断Map中是否含有指定key的元素
 * containsValue(value) 判断Map中是否含有指定value的元素
 * keys() 获取Map中所有key的数组（array）
 * values() 获取Map中所有value的数组（array）
 *
 */
usingNamespace("Base")["Map"] = {
    Map: function () {
        this.elements = new Array();

        //获取Map元素个数
        this.size = function () {
            return this.elements.length;
        },

            //判断Map是否为空
            this.isEmpty = function () {
                return (this.elements.length < 1);
            },

            //删除Map所有元素
            this.clear = function () {
                this.elements = new Array();
            },

            //向Map中增加元素（key, value)
            this.put = function (_key, _value) {
                if (this.containsKey(_key) == true) {
                    if (this.containsValue(_value)) {
                        if (this.remove(_key) == true) {
                            this.elements.push({
                                key: _key,
                                value: _value
                            });
                        }
                    } else {
                        this.elements.push({
                            key: _key,
                            value: _value
                        });
                    }
                } else {
                    this.elements.push({
                        key: _key,
                        value: _value
                    });
                }
            },

            //删除指定key的元素，成功返回true，失败返回false
            this.remove = function (_key) {
                var bln = false;
                try {
                    for (i = 0; i < this.elements.length; i++) {
                        if (this.elements[i].key == _key) {
                            this.elements.splice(i, 1);
                            return true;
                        }
                    }
                } catch (e) {
                    bln = false;
                }
                return bln;
            },

            //获取指定key的元素值value，失败返回null
            this.get = function (_key) {
                try {
                    for (i = 0; i < this.elements.length; i++) {
                        if (this.elements[i].key == _key) {
                            return this.elements[i].value;
                        }
                    }
                } catch (e) {
                    return null;
                }
            },

            //获取指定索引的元素（使用element.key，element.value获取key和value），失败返回null
            this.element = function (_index) {
                if (_index < 0 || _index >= this.elements.length) {
                    return null;
                }
                return this.elements[_index];
            },

            //判断Map中是否含有指定key的元素
            this.containsKey = function (_key) {
                var bln = false;
                try {
                    for (i = 0; i < this.elements.length; i++) {
                        if (this.elements[i].key == _key) {
                            bln = true;
                        }
                    }
                } catch (e) {
                    bln = false;
                }
                return bln;
            },

            //判断Map中是否含有指定value的元素
            this.containsValue = function (_value) {
                var bln = false;
                try {
                    for (i = 0; i < this.elements.length; i++) {
                        if (this.elements[i].value == _value) {
                            bln = true;
                        }
                    }
                } catch (e) {
                    bln = false;
                }
                return bln;
            },

            //获取Map中所有key的数组（array）
            this.keys = function () {
                var arr = new Array();
                for (i = 0; i < this.elements.length; i++) {
                    arr.push(this.elements[i].key);
                }
                return arr;
            },

            //获取Map中所有value的数组（array）
            this.values = function () {
                var arr = new Array();
                for (i = 0; i < this.elements.length; i++) {
                    arr.push(this.elements[i].value);
                }
                return arr;
            };
        return this;
    }

};
var $Map = Base.Map;

usingNamespace("Base")["String"] = {
    trim: function (str) {
        if (str == null || typeof str == "undefined") {
            return "";
        }
        return str.replace(/(^\s*)|(\s*$)/g, "");
    },

    isNull: function (object) {
        if (object == null || typeof object == "undefined") {
            return true;
        }
        return false;
    },

    isEmpty: function (str) {
        if (str == null || typeof str == "undefined" ||
            str == "") {
            return true;
        }
        return false;
    },

    isBlank: function (str) {
        if (str == null || typeof str == "undefined" ||
            str == "" || Base.String.trim(str) == "") {
            return true;
        }
        return false;
    },

    isNullOrBlank: function (str) {
        return Base.String.isNull(str) || Base.String.isBlank(str);
    }
};
var $String = Base.String;

//添加的蒙版
usingNamespace("Base")["Mask"] =  {
    AddLogo: function(content){
        if ($String.isNullOrBlank(content)) {
            content = "";
        }
        var str = "<div class=\"loading-mask\" style='opacity: 0.4; position:absolute; left:0; top:0; width:100%; height:100%; z-index:20000; background-color:#ededed;'></div>" +
            "    <div class=\"loading\" style='position:absolute; left:45%; top:40%;padding:2px; z-index:20001; height:auto;'>" +
            "    <div class=\"loGo\" style='background:#ededed;color:#444;font:bold 20px tahoma,arial,helvetica;padding:10px;margin:0;height:auto;'>" +
            "    <img style='height: auto;max-width: 100%;vertical-align: middle;border: 0; width: 100px;' src='/static/images/load.gif'>" +
            "    <p>正在加载</p >" +
            "    </div>" +
            "    </div>";
        var logo=$(str);
        $("body").append(logo);
    },
    RemoveLogo: function(){
        $(".loGo").remove();
    }
};

var $Mask = Base.Mask;