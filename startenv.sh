#!/bin/sh

export CATALINA_OPTS="$CATALINA_BASE/cache -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9002";

export JAVA_OPTS="-XX:-DisableExplicitGC \
-server -Xms600m -Xmx600m -Xmn500m -Xss256k \
-XX:+DisableExplicitGC -Xnoclassgc -Xverify:none \
-XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -Xloggc:$CATALINA_BASE/logs/gc.log-server \
-Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false \
-XX:+TieredCompilation \
-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 \
-Djava.util.Arrays.useLegacyMergeSort=true"
export LC_ALL=en_US.UTF-8

export TOMCAT_USER=tomcat
chown -R tomcat:tomcat /mc/webapp/tomcat9_bg
chown -R tomcat:tomcat $CATALINA_BASE/webapps/ROOT
chown -R tomcat:tomcat $CATALINA_BASE/conf
chown -R tomcat:tomcat $CATALINA_BASE/logs
chown -R tomcat:tomcat $CATALINA_BASE/work
chown -R tomcat:tomcat $CATALINA_BASE/cache
chown -R tomcat:tomcat $CATALINA_BASE/temp
