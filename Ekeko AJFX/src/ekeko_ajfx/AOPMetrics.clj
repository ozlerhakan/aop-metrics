(ns ekeko-ajfx.AOPMetrics
  ^{:doc "Specific Aspect-oriented programming Metrics"
    :author "Hakan Ozler - ozler.hakan[at]gmail.com" }
  (:refer-clojure :exclude [== type declare class])
  (:require [clojure.core.logic :as l]
            [clojure.java.io :as io]
            [damp.ekeko.aspectj  
             [weaverworld :as w]  
             [soot :as ajsoot]]
            [damp.ekeko.soot     
             [soot :as jsoot]])
  (:use [inspector-jay.core]
        [clojure.repl]
        [damp.ekeko logic]
        [damp.ekeko ekekomodel]
        [damp.ekeko gui]
        [damp.ekeko]
        [clojure.inspector :exclude [inspect]])
  (:import [soot.jimple IdentityStmt]
           [ekeko_ajfx CountingComments]
           [org.aspectj.lang Signature]))

 (comment )

 ;############################### METRIC LOC ###############################

 (defn class-loc [filePath ignoredTestName]
   "count the number of lines of java code in a given project - except blank lines"
   (reduce
    +
    (for [file (file-seq  filePath) :when (and 
                                            (.endsWith (.toString file )".java") 
                                            (false? (lastIndexOfText (.getName file) ignoredTestName)) 
                                            (false? (or 
                                                      (.startsWith (.toLowerCase (.getName file)) "test") 
                                                      (.endsWith   (.toLowerCase (.getName file)) "test"))))]
      (with-open [rdr (io/reader  file)] (count  (filter #(re-find #"\S" %) (line-seq rdr)))))))
 ;(class-loc (io/file"C:/Users/HAKAN/runtime-New_configuration-clojure/HealthWatcherAspectJ/src") "MainTST")
 
 (defn aspect-loc [filePath]
   "count the number of lines of aspectj code in a given project - except blank lines"
   (reduce
     +
     (for [file (file-seq  filePath) :when (.endsWith (.toString file )".aj")]
       (with-open [rdr (io/reader file)] (count  (filter #(re-find #"\S" %) (line-seq rdr)))))))
 ;(aspect-loc (io/file"C:/Users/HAKAN/runtime-New_configuration-clojure/AJTestMetrics/src"))
 
 (defn java-docs [filePath ignoredTestName]
   "count the number of lines of comments & javadocs"
   (reduce
     +
     (for [file (file-seq  filePath) :when (and 
                                             (or 
                                               (.endsWith (.toString file )".aj") 
                                               (.endsWith (.toString file )".java")) 
                                             (false? (lastIndexOfText (.getName file) ignoredTestName)) 
                                             (false? (or 
                                                       (.startsWith (.toLowerCase (.getName file)) "test") 
                                                       (.endsWith   (.toLowerCase (.getName file)) "test"))))]
       (.getComments (new CountingComments)  (.toString file)))))
 ;(java-docs (io/file"C:/Users/HAKAN/runtime-New_configuration-clojure/HealthWatcherAspectJ/src") "MainTST")  
       
 (defn LOC [filepath ignore]
     ( - 
       (+ 
         (class-loc (io/file filepath) ignore) 
         (aspect-loc (io/file filepath)))
       (java-docs (io/file filepath) ignore)))
 
 ;(LOC "C:/Users/HAKAN/runtime-New_configuration-clojure/HealthWatcherAspectJ/src" "MainTST")
 ;(LOC "C:/Users/HAKAN/runtime-New_configuration-clojure/Version18/Version18/src" "MainTST")
 
 ;############################### METRIC VS ############################### 
 (defn NOClasses [?classes]
   "Number of Classes in the project except enums, interfaces, their inner classes!!, and a test class."
   (l/all
    (w/class ?classes)
     (equals false (or 
                     (.isEnum ?classes)
                     (IndexOfText (.getName ?classes) "$");this line excludes all nested-classes!
                     (lastIndexOfText (.getName ?classes) "MainTST");our initial main to activate soot analysis, so I ignore it
                     (IndexOfText (.getName ?classes) "lang.Object")
                     (.startsWith (.getSimpleName ?classes) "Test")
                     (.endsWith (.getSimpleName ?classes) "Test")))))
 
 (inspect  (sort-by first (ekeko [?cn] (l/fresh [?c] (NOClasses ?c) (equals ?cn (.getName ?c))))))
 
 (defn- CountNOClasses []
   (count (ekeko [?cn] (l/fresh [?c] (NOClasses ?c) (equals ?cn (.getName ?c))))))
 (CountNOClasses)
 
 (defn- findClass [?name]
   (first (ekeko [?c]
                 (NOClasses ?c)
                 (succeeds (= ?name (.getName ?c))))))
 
 (defn NOAspects [?aspects ?source]  
   "the number of aspects in a selected project"
   (l/all
     (w/aspect ?aspects) 
     (equals ?source (.getSourceLocation ?aspects))))
 
 (inspect (sort-by first (ekeko [?an] (l/fresh [?as ?sour] (NOAspects ?as ?sour) (equals ?an (.getName ?as))))))
 (count (ekeko [?as ?sour] (NOAspects ?as ?sour)))

 (defn VS []
   (clojure.set/union  (ekeko [?as] (l/fresh [?sour] (NOAspects ?as ?sour)))
                       (ekeko [?c] (NOClasses ?c))))
 (VS)
 
 (defn CalculateVS []
   (+  (count (ekeko [?as ?sour] (NOAspects ?as ?sour)))
       (count (ekeko [?c] (NOClasses ?c)))))
 (CalculateVS)
 ;############################### METRIC NOAttributes (fields) ###############################

 (defn NOA [?module ?field]
   "count fields both CLASSES and ASPECTS except their nested-classes' fields"
   (l/fresh [?fields ?modules]
            (equals ?modules (VS))
            (contains ?modules ?module)
            (equals ?fields (.getDeclaredFields  (first ?module)))
            (contains ?fields ?field)  
            (equals false (IndexOfText (.getName ?field) "$"))))
 
 (inspect (sort-by first (ekeko [?cn ?f] (l/fresh [?c] (NOA ?c ?f ) (equals ?cn (.getName (first ?c)))))))
 (count (ekeko [?c ?f] (NOA ?c ?f))) 

 ;############################### METRIC NOOperations (methods and advices) ###############################

 (defn NOM [?module ?method]
   "counts the number of methods declarations of classes and aspects"
   (l/fresh [?types ?modules ?methods]
            (equals ?modules (VS))
            (contains ?modules ?module)
            (equals ?methods (.getDeclaredMethods (first ?module)))
            (contains ?methods ?method)
            (equals false (.isAbstract ?method))
            (equals false (= 3 (.getKey (.getKind ?method))))
            (equals false (= 8 (.getModifiers ?method)))
            (equals false (IndexOfText (.getName ?method) "$"))
            (equals false (or (= "hasAspect" (.getName ?method)) 
                              (= "aspectOf" (.getName ?method))))))
 
 (inspect (sort-by first (ekeko [?module ?method] (l/fresh[?m] (NOM ?m ?method) (equals ?module (.getName (first ?m)))))))
 (count ( ekeko [?types ?m] (NOM ?types ?m)))
 
 (defn- aspects-methods [?types ?methods]
   (l/fresh [?tname ?source ?methodName]
            (NOAspects ?types ?source)
            (w/type-method ?types ?methods)
            (equals ?tname (.getClassName ?types))
            (equals ?methodName (.getName ?methods))
            (equals false (= 8 (.getModifiers ?methods)))
            (equals false (.startsWith (.getName ?methods) "ajc"))
            (equals false (or (= "hasAspect" (.getName ?methods)) 
                              (= "aspectOf" (.getName ?methods)) 
                              (= "<init>" (.getName ?methods))))))
  
 (inspect (sort-by first  (ekeko [?tn ?m] (l/fresh [?t] (aspects-methods ?t ?m) (equals ?tn (.getClassName ?t))))))
 (count (ekeko [?tn ?m] (l/fresh [?t] (aspects-methods ?t ?m) (equals ?tn (.getClassName ?t)))))
 ;############################### METRIC NOIntertype methods ############################### 

 (defn NOIntertype [?i] 
   "get the all intertype method declarations implemented in a project except abstract intertype method declarations"
   (l/all
            (w/intertype|method ?i)
            (equals false (.isAbstract (.getSignature ?i)))))
 
  (inspect (ekeko [?i] (NOIntertype ?i)))
  (count (ekeko [?i] (NOIntertype ?i)))

 ;############################### METRIC NOAdvices ############################### 
 
 (defn NOAdvices [?aspect  ?adv ?pointcut]  
   "Counts the number of Advices"
   (l/fresh []
            (w/advice ?adv)
            (equals false (IndexOfText (.getName (.getClass ?adv)) "Checker"))
            (equals false (or
                            (or (.isCflow (.getKind ?adv)) 
                                (.isPerEntry (.getKind ?adv)))
                            (= "softener" (.getName (.getKind ?adv)))));I must exclude perThis, perTarget , perCflow, Cflow, CflowBelow, softener, and Checker(warning)!!
            (equals ?aspect  (.getDeclaringType (.getSignature ?adv)))
            (equals ?pointcut (findpointcut ?adv))))

(inspect (sort-by first (ekeko [?an ?p ?adv] (l/fresh [?a] (NOAdvices ?a ?adv ?p) (equals ?an (.toString ?a))))))

(defn- COUNTNOAdvices [] 
  (count (ekeko [?a ?adv ?p] (NOAdvices ?a ?adv ?p))))
(COUNTNOAdvices)

(defn- findpointcut [advice]
  (if (= "" (.toString (.getPointcut advice))) (getPointcut-findpointcut advice) (.getPointcut advice)))
(defn- getPointcut-findpointcut [?advice]
  (first (first (ekeko [?pcut]
                       (l/fresh [?p ?pn]
                                (w/pointcutdefinition ?p)
                                (equals ?pn (.getName ?p))
                                (equals ?pcut (.getPointcut ?p))
                                (succeeds (= ?pn (.name (.getPointcut (.getAssociatedShadowMunger (.getSignature ?advice)))))))))))

 ;############################### Advice-Method dependence (AM) ###############################
 (defn AM [?aspectName ?calledmethods ?soot|methodName]
    "the number of method calls per advice body"
       (l/fresh [?soot|method ?advice ?aspect ?soot ?pointcut]
                (NOAdvices ?aspect ?advice ?pointcut)
                (ajsoot/advice-soot|method ?advice ?soot|method)
                (NOMethodCalls ?soot|method ?aspectName ?calledmethods ?soot|methodName)))
 
 (inspect (sort-by first (ekeko [?aspectName ?calledmethods ?soot|methodName] (AM ?aspectName ?calledmethods ?soot|methodName))))
 (count (sort-by first (ekeko [?aspectName ?calledmethods ?soot|methodName] (AM ?aspectName ?calledmethods ?soot|methodName)))) 
 ;############################### IntertypeMethod-Method dependence (IM) ###############################
 (defn IM [?aspectName ?calledmethods ?soot|interName  ]
   "the number of method calls per intertype method declaration body"
   (l/fresh [?itmethod ?units ?unit  ?inter|method ?soot|methodName ?soot]
            (ajsoot/intertype|method-soot|method ?itmethod ?soot)
            (succeeds (.hasActiveBody ?soot))
            (equals ?units (.getUnits (.getActiveBody ?soot)))
            (contains ?units ?unit)
            (succeeds (.containsInvokeExpr ?unit))
            (succeeds (or (= "soot.jimple.internal.JAssignStmt" (.getName (.getClass ?unit)))  
                          (= "soot.jimple.internal.JInvokeStmt" (.getName (.getClass ?unit)))))
            (succeeds (.containsInvokeExpr ?unit))
            (equals ?inter|method (.getMethod (.getInvokeExpr ?unit)))
            (NOMethodCalls ?inter|method ?aspectName ?calledmethods ?soot|methodName)
            (equals ?soot|interName (str "InterMethod {"(subs ?soot|methodName (+ (.lastIndexOf ?soot|methodName "$") 1))))))
 
 (inspect (sort-by first (ekeko [ ?aspectName ?calledmethods ?soot|interName ] (IM  ?aspectName ?calledmethods ?soot|interName ))))
 (count (ekeko [ ?aspectName ?calledmethods ?soot|interName ] (IM  ?aspectName ?calledmethods ?soot|interName )))
 
 (inspect (ekeko [?soot]
                 (l/fresh [ ?itmethod]
                          (ajsoot/intertype|method-soot|method ?itmethod ?soot)
                          (equals true (= (.getName ?soot) "aspectUpdating")))))
 
 ;############################### Method-Method dependence (MM) ###############################
 (defn MM [ ?aspectName ?calledmethods ?soot|methodName]
   "the number of method calls per method body declared in aspects"
   (l/fresh [?aspect ?ajmethod ?soot|method]
            (aspects-methods ?aspect ?ajmethod)
            (ajsoot/method-soot|method ?ajmethod ?soot|method);new function has been created in the Ekeko AspectJ project -> /EkekoAspectJ/src/damp/ekeko/aspectj/soot.clj Line: 113
            (NOMethodCalls ?soot|method ?aspectName ?calledmethods ?soot|methodName)))
 
 (inspect (sort-by first (ekeko [ ?aspectName  ?soot|methodName ?calledmethods]   (MM ?aspectName ?calledmethods ?soot|methodName))))
 (count (ekeko [ ?aspectName  ?soot|methodName ?calledmethods] (MM  ?aspectName ?calledmethods ?soot|methodName)))

;################################## Main Function for the above 3 Metrics, It is working.. (I hope so) ####################################
 (defn- NOMethodCalls [?soot|method ?aspectName ?calledmethods ?soot|methodName]
   (l/fresh [?units ?sootMDeclass]
            (succeeds (.hasActiveBody ?soot|method))
            (soot|unit-getDeclarationClassname ?soot|method ?aspectName)
            (equals ?units (.getUnits (.getActiveBody ?soot|method)))
            (equals ?soot|methodName (str "Method {"(.getName ?soot|method)"}"))
            (contains ?units ?calledmethods)
            (succeeds (.containsInvokeExpr ?calledmethods))
            (succeeds (or (= "soot.jimple.internal.JAssignStmt" (.getName (.getClass ?calledmethods)))  
                          (= "soot.jimple.internal.JInvokeStmt" (.getName (.getClass ?calledmethods)))))	          
            (equals ?sootMDeclass (.getShortName (.getDeclaringClass (.getMethod (.getInvokeExpr ?calledmethods)))))
            (equals false (or  
                            (IndexOfText  ?sootMDeclass "StringBuilder")
                            (IndexOfText  ?sootMDeclass "StringBuffer")
                            (IndexOfText  ?sootMDeclass "aspectj")
                            (IndexOfText  ?sootMDeclass "apache")
                            (IndexOfText  ?sootMDeclass "Iterator")
                            (IndexOfText  ?sootMDeclass "CFlowCounter")
                            (IndexOfText  ?sootMDeclass "CFlowStack")
                            (IndexOfText  ?sootMDeclass "Factory")
                            (IndexOfText  ?sootMDeclass "Conversions")))
            (equals false (= "<init>" (.getName (.getMethod (.getInvokeExpr ?calledmethods)))))
            (equals false
                    (and
                      (.startsWith (.toString (.getInvokeExpr ?calledmethods)) "staticinvoke")
                      (IndexOfText (.getName  (.getMethod (.getInvokeExpr ?calledmethods))) "$")
                      (.containsInvokeExpr    (.getFirstNonIdentityStmt (.getActiveBody (.getMethod (.getValue (.getInvokeExprBox ?calledmethods))))))
                      (or
                        (.startsWith (.name (.getMethodRef (.getValue (.getInvokeExprBox (.getFirstNonIdentityStmt (.getActiveBody (.getMethod (.getValue (.getInvokeExprBox ?calledmethods))))))))) "ajc$set")
                        (.startsWith (.name (.getMethodRef (.getValue (.getInvokeExprBox (.getFirstNonIdentityStmt (.getActiveBody (.getMethod (.getValue (.getInvokeExprBox ?calledmethods))))))))) "ajc$get"))))
            (equals false
                    (and
                      (or
                        (.startsWith (.toString (.getInvokeExpr ?calledmethods)) "staticinvoke")
                        (.startsWith (.toString (.getInvokeExpr ?calledmethods)) "virtualinvoke"))
                      (or
                        (=               (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "valueOf")
                        (lastIndexOfText (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "$advice")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$if$")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$get$")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$set$")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$afterReturning$")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$around$")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$inlineAccessFieldGet$")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$inlineAccessFieldSet$")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$interFieldSetDispatch$")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$interFieldGetDispatch$")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$before$")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$after$")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$afterThrowing$")
                        (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "access$"))))
            (equals false
                    (or
                      (= "aspectOf" (.getName (.getMethod (.getValue (.getInvokeExprBox ?calledmethods)))))
                      (= "makeJP" (.getName (.getMethod (.getValue (.getInvokeExprBox ?calledmethods)))))
                      (= "iterator" (.getName (.getMethod (.getValue (.getInvokeExprBox ?calledmethods)))))))))
 
 ;############################### Attribute-Class dependence (AtC) ###############################
 ;Definition : if a class is the type of an field of an aspect 
 ;Filtering primitive types and interfaces that could be the type of a field!
  (defn AtC [?aspectName ?fieldName ?fieldTypeName] 
    "count the number of types of fields of aspects"
         (l/fresh [?field ?aspect ?isSameInterface ?fieldType]
                 (w/type-field ?aspect ?field)
                 (succeeds (.isAspect ?aspect))
                 (equals ?aspectName (str "Aspect {"(.getName ?aspect)"}"))
                 (equals ?fieldType  (.getType ?field))
                 (equals false       (.isPrimitiveType (.getType ?field))); I ignore primitive types such as boolean, int , void ,double, and so on.
                 (equals false       (or (.startsWith (.getName ?field) "ajc") (.startsWith (.getName ?field) "this")))
                 (equals ?fieldTypeName     (.getName ?fieldType))
                 (equals ?isSameInterface (getInterface ?fieldTypeName))
                 (equals true        (nil? ?isSameInterface));check whether the type is interface or not!!
                 (equals ?fieldName  (str "<Field name: " (.getName ?field) ">"))))
 
 (inspect (sort-by first (ekeko [?typef ?f ?t] (AtC ?t ?f ?typef))))
 (count (ekeko [?typef ?f ?t] (AtC ?t ?f ?typef)))
 
 ;############################### Advice-Class  dependence (AC) ###############################
 ; if a class is  the type of a parameter of a piece of advice of an aspect 
 (defn getAC-p1 [?aspectSN ?adviceKind ?typename]
   (l/fresh [?aspect ?typesofAdvice ?advice  ?isInterface  ?parameter ?pointcut]
            (NOAdvices ?aspect ?advice ?pointcut)
            (equals ?aspectSN (str "Aspect {"(.getSimpleName ?aspect)"}"))
            (equals   ?adviceKind (.getKind ?advice))
            (equals   ?typesofAdvice (.getParameterTypes (.getSignature ?advice)))
            (contains ?typesofAdvice ?parameter)
            (equals   ?typename  (.getName ?parameter))
            (equals false (.isPrimitiveType ?parameter))
            (equals false (= (.getName ?parameter) "org.aspectj.runtime.internal.AroundClosure"))
            (equals   ?isInterface (getInterface ?typename));control whether a selected type is interface that was implemented in a given AspectJ app 
            (succeeds (nil? ?isInterface))
            (equals false (= "int[]" ?typename))))           
 
 (inspect  (sort-by first (ekeko [?as ?a ?r] (getAC-p1 ?as ?a ?r )))) 
 
 ; the return type of the piece of advice - around - ; "after returning" "after throwing" are being checked in the above function called -getAC-p1-
 (defn getAC-p2 [?aspectSN ?adviceKind ?returntypename] 
   (l/fresh [?aspect ?advice ?isInterface ?returntype ?pointcut]
            (NOAdvices ?aspect ?advice ?pointcut)
            (equals ?aspectSN (str "Aspect {"(.getSimpleName ?aspect)"}"))
            (equals ?adviceKind (.getKind ?advice))
            (succeeds (= 5 (.getKey (.getKind ?advice))))
            (equals ?returntype (.getReturnType (.getSignature ?advice)))
            (equals ?returntypename (.getName ?returntype))
            (equals false (.isPrimitiveType ?returntype))
            (equals ?isInterface (getInterface ?returntypename))
            (succeeds  (nil? ?isInterface))
            (equals false (= "int[]" ?returntypename))))
  
  (inspect  (sort-by first (ekeko [?r ?as ?a] (getAC-p2 ?as ?a ?r))))
  
  ;combined the two queries in one inspect
  (inspect (sort-by first  (clojure.set/union
                            (ekeko [?vari ?as ?ad ] (getAC-p1 ?as ?ad ?vari))
                            (ekeko [?vari ?as ?ad ] (getAC-p2 ?as ?ad ?vari)))))
  (count  (clojure.set/union
            (ekeko [?vari ?as ?ad ] (getAC-p1 ?as ?ad ?vari))
            (ekeko [?vari ?as ?ad ] (getAC-p2 ?as ?ad ?vari))))
 ;############################### Intertype method-Class dependence (IC) ###############################
 ;if classes are the type of  parameters or return type of intertype method declarations in aspects of a given AspectJ App
 
 (defn measureIC-returnType [?aspect ?interName ?type ?returnname] 
   "find all return types of intertype method declarations"
   (l/fresh [?isInterface ?i ?return] 
            (NOIntertype ?i)
            (equals ?aspect (str "Aspect {"(.getName (.getAspectType ?i))"}"))
            (equals ?return (.getReturnType (.getSignature ?i)))
            (equals false (.isPrimitiveType ?return));except primitive types
            (equals ?returnname  (.getName ?return))
            (equals ?isInterface (getInterface ?returnname));except interfaces
            (succeeds  (nil? ?isInterface))
            (equals ?type (str "RETURN"))
            (equals ?interName (str (.getClassName (.getDeclaringType (.getSignature (.getMunger ?i))))"."(.getName (.getSignature ?i))))))
 
 (inspect (sort-by first  (ekeko [?aspect ?interName ?type ?return] (measureIC-returnType ?aspect ?interName ?type ?return))))

 (defn measureIC-parameters [?aspect ?interName ?param ?variName] 
   "find all parameter types of intertype method declarations"
   (l/fresh [?v ?isInterface ?i ?vari] 
            (NOIntertype ?i)
            (equals ?aspect (str "Aspect {"(.getName (.getAspectType ?i))"}"))
            (equals ?v (.getParameterTypes (.getSignature ?i)))
            (contains ?v ?vari)
            (equals false (.isPrimitiveType ?vari));except primitive types
            (equals ?variName  (.getName ?vari))
            (equals ?isInterface (getInterface ?variName));except interfaces
            (succeeds  (nil? ?isInterface))
            (equals ?param (str "PARAM"))
            (equals ?interName (str (.getClassName (.getDeclaringType (.getSignature (.getMunger ?i))))"."(.getName (.getSignature ?i))))))
 
 (inspect (sort-by first  (ekeko [?aspect ?interName ?type ?variName] (measureIC-parameters ?aspect ?interName ?type ?variName))))

  ;combined the two queries in one inspect
  (inspect (sort-by first (clojure.set/union
                            (ekeko [?vari ?as ?a ?t ] (measureIC-returnType ?as ?a ?t ?vari))
                            (ekeko [?vari ?as ?a ?t ] (measureIC-parameters ?as ?a ?t ?vari)))))
  (count (clojure.set/union
           (ekeko [?vari ?as ?a ?t ] (measureIC-returnType ?as ?a ?t ?vari))
           (ekeko [?vari ?as ?a ?t ] (measureIC-parameters ?as ?a ?t ?vari))))
  
 ;############################### Method-Class dependence (MC) ###############################
 ;Definition: if classes are the type(s) of parameters or return type(s) of method declarations in aspects of a given AspectJ App
 (defn measureMC-param [?aspectn ?methodN ?paramName ?rtype] 
   (l/fresh [?aspect ?params ?method ?param ?isInterface]
            (aspects-methods ?aspect ?method)
            (equals ?aspectn (str "Aspect {"(.getSimpleName ?aspect) "}"))
            (equals ?params (.getParameterTypes ?method))
            (contains ?params ?param)
            (equals false (.isPrimitiveType ?param))
            (equals ?paramName (.getName ?param))
            (equals false (= "int[]" ?paramName))
            (equals ?isInterface (getInterface ?paramName));except interface classes
            (succeeds  (nil? ?isInterface))
            (equals ?methodN (str "<Method Name: "(.getName ?method)">"))
            (equals ?rtype (str "PARAM"))))
  
 (inspect (sort-by first  (ekeko [?p ?A ?m ?r] (measureMC-param ?A ?m ?p ?r))))
  
 (defn measureMC-return [?aspectn ?methodN ?returnName ?rtype] 
   (l/fresh [?aspect ?return ?method ?isInterface]
            (aspects-methods ?aspect ?method)
            (equals ?aspectn (str "Aspect {"(.getSimpleName ?aspect) "}"))
            (equals ?return (.getReturnType ?method))
            (equals false (.isPrimitiveType ?return))
            (equals ?returnName (.getName ?return))
            (equals false (= "int[]" ?returnName))
            (equals ?isInterface (getInterface ?returnName));except interface classes
            (succeeds  (nil? ?isInterface))
            (equals ?methodN (str "<Method Name: "(.getName ?method)">"))
            (equals ?rtype (str "RETURN"))))
  
  (inspect (sort-by first  (ekeko [?p ?A ?m ?r] (measureMC-return ?A ?m ?p ?r))))
   
  ;combined the two queries in one inspect
  (inspect (sort-by first (clojure.set/union
                            (ekeko [?vari ?A ?m ?r] (measureMC-param ?A ?m ?vari ?r))
                            (ekeko [?vari ?A ?m ?r] (measureMC-return ?A ?m ?vari ?r)))))
  (count (clojure.set/union
                            (ekeko [?vari ?A ?m ?r] (measureMC-param ?A ?m ?vari ?r))
                            (ekeko [?vari ?A ?m ?r] (measureMC-return ?A ?m ?vari ?r))))
  
  ;############################### Pointcut-Class dependence (PC) ###############################
  ;if a class is the type of a parameter of a pointcut (poincutDefinition) in an aspect
  (defn PC [?typename ?pointcutname ?aspect] 
    "counts pointcut-class dependencies"
    (l/fresh [?pointcut ?types ?type ?isInterface]
             (w/pointcutdefinition ?pointcut)
             (equals ?types (.getParameterTypes ?pointcut))
             (contains ?types ?type)
             (equals false  (.isPrimitiveType ?type))
             (equals ?typename    (.getName ?type))
             (equals ?isInterface (getInterface ?typename))
             (succeeds  (nil? ?isInterface))
             (equals ?aspect (.getName (.getDeclaringType ?pointcut)))
             (equals ?pointcutname (str "<Pointcut Name :"(.getName ?pointcut)">"))))
  
  (inspect (sort-by first (ekeko [?tn ?pn ?aspect] (PC ?tn ?pn ?aspect))))
  (count (ekeko [?tn ?pn ?aspect] (PC ?tn ?pn ?aspect)))
  
  ;############################### Pointcut-Method dependence (PM) ###############################
  ;if a pointcut of an aspect contains at least one join point that is related to a method/construct of a class
 (defn countPM [?calledClass ?calledMth  ?aspectName ?adviceKind ?pointcut ?shadow] 
            (l/fresh [?toLongStringmethod ?aspect ?advice ?shadowParent ?fullyClassName ?class ?methodName]
                    (NOAdvices ?aspect ?advice ?pointcut)
                    (w/advice-shadow ?advice ?shadow);in order to reach the join point shadows, I used w/advice-shadow to pick them up along with advices' pointcut
                    (equals true (.isCode ?shadow))
                    (equals ?shadowParent (.getParent (.getParent ?shadow)))
                    (succeeds  (or (.startsWith (.getName ?shadow) "method-call") 
                                   (.startsWith (.getName ?shadow) "constructor-call")))
                    (succeeds  (= "class" (.toString (.getKind ?shadowParent))));we only want to show class - method/construct calls!
                    (equals ?fullyClassName (str (.getPackageName ?shadowParent)"."(.getName ?shadowParent)))
                    (equals ?aspectName (str "Aspect {"(.getSimpleName ?aspect)"}"))
                    (equals ?calledMth (str "In Class: " (.getName ?shadowParent)" -> "(.toString ?shadow)))
                    (equals ?adviceKind (str "Advice {"(.getKind ?advice)"}"))
                    (equals ?toLongStringmethod (first (clojure.string/split (first (rest (clojure.string/split (.getName ?shadow) #" "))) #"\(")))
                    (equals ?class (subs ?toLongStringmethod 0 (.lastIndexOf ?toLongStringmethod ".")))
                    (equals ?calledClass (str "<Class Name :" ?class ">"))
                    (equals ?methodName (subs ?toLongStringmethod (+ (.lastIndexOf ?toLongStringmethod ".") 1)))
                    (succeeds (= ?fullyClassName ?class))
                    (equals false (nil? (isParentsChild ?methodName ?shadowParent)))))
  
 (inspect (sort-by first  (ekeko [?CalledM  ?calledC  ?aspect ?adv ?pnt ?shadow ] (countPM ?calledC ?CalledM ?aspect ?adv ?pnt ?shadow ))))
 (count (ekeko [?CalledM  ?calledC  ?aspect ?adv ?pnt ?shadow] (countPM ?calledC ?CalledM ?aspect ?adv ?pnt ?shadow)))

 (defn- isParentsChild [?methodname ?parent]
   (first (ekeko [ ?childname ]
                 (l/fresh [?isparent  ?child ?listofchildren]
                          (equals ?listofchildren (.getChildren ?parent))
                          (contains ?listofchildren ?child)
                          (succeeds (= "method" (.toString (.getKind ?child))))
                          (equals ?childname (.getName ?child))
                          (equals true (= ?methodname ?childname))))))
 ;output ex: mobilemedia-> 
 ;["In Class: MediaListController -> method-call(void lancs.mobilemedia.core.ui.controller.MediaListController.appendMedias(lancs.mobilemedia.core.ui.datamodel.MediaData[], lancs.mobilemedia.core.ui.screens.MediaListScreen))" 
 ; "<Class Name :lancs.mobilemedia.core.ui.controller.MediaListController>" 
 ; "Aspect {SortingAspect}" 
 ; #<AdviceKind before> 
 ; #<AndPointcut (((call(public void lancs.mobilemedia.core.ui.controller.MediaListController.appendMedias(lancs.mobilemedia.core.ui.datamodel.MediaData[], lancs.mobilemedia.core.ui.screens.MediaListScreen)) && this(BindingTypePattern(lancs.mobilemedia.core.ui.controller.MediaListController, 0))) && args(BindingTypePattern(lancs.mobilemedia.core.ui.datamodel.MediaData[], 1), BindingTypePattern(lancs.mobilemedia.core.ui.screens.MediaListScreen, 2))) && persingleton(lancs.mobilemedia.optional.sorting.SortingAspect))>]
 ; The output says that a declared pointcut that connects with a before advice in SortingAspect refers to a method which is "appendMedias" in MediaListController
 ; so, this method is belongs to the class, in other words,a join point shadow matches the method called "appendMedias" of MediaListController  
 
 ;############################### AdvanceAdvice : How many advice depend on constructs that can only be determined at runtime? ###############################
 ;count NOAAdvice that have a poincut has at least one of the advance primitive pointcuts such as if, adviceexecution, cflow, and cflowbelow.
  (defn NOAAdvice [?shortAspect ?advicekind ?pointcut ?ar]
                            (l/fresh [?aspect ?advice ?advices]
                                     (NOAdvices ?aspect ?advice ?pointcut)
                                     (equals ?shortAspect (str "Aspect {"(.getSimpleName ?aspect)"}"))
                                     (equals ?advicekind (str "Advice {"(.toString (.getKind ?advice))"}"))
                                     (getAdvance ?pointcut ?advices)
                                     (equals ?ar (vec (into #{} ?advices)))
                                     (equals false (empty? ?ar))))
  
 (inspect  (sort-by first (ekeko [?shortAspect ?advicekind ?list ?pointdefs](NOAAdvice ?shortAspect ?advicekind ?pointdefs ?list))))
 (count (ekeko [?shortAspect ?advicekind ?list ?pointdefs] (NOAAdvice ?shortAspect ?advicekind ?pointdefs ?list)))
  
 (defn- getAdvance [?pointcut ?arglist]
   "it is responsible for the primitive pointcut called \"getAdvance\" "
   (l/fresh [?result ?y]
     (equals ?result (java.util.ArrayList. []))
     (equals ?y (Advance ?pointcut ?result))
     (equals ?arglist ?result)))
 
 (defn- Advance [pointcut res]
   "a nested function \"Advance\" is to pick up the related primitive poincuts!"
   (if  (or (= (getPKind pointcut) 5 ) (= (getPKind pointcut) 6))
      [(Advance (getLeft pointcut) res) (Advance (getRight pointcut) res)]
      (if (and (= (getPKind pointcut) 1) (= "adviceexecution" (.toString (getKind pointcut)))) (addlist res pointcut) 
         (if (or (= (getPKind pointcut) 9) (= (getPKind pointcut) 10)) (addlist res pointcut) 
           (if (= (getPKind pointcut) 7) 
             (if (or (= (getPKind (.getNegatedPointcut pointcut)) 9) (= (getPKind (.getNegatedPointcut pointcut)) 10) (and (= (getPKind (.getNegatedPointcut pointcut)) 1) (= "adviceexecution" (.toString (getKind (.getNegatedPointcut pointcut)))))) (addlist res pointcut)))))))
  
 ;############################### BasicAdvice : How many advice have a pointcut that uses more than the basic primitive pointcuts? ###############################
 (defn NOBAdvices [?total]
   (l/fresh [?advicesize ?advancesize ?aspect ?advice ?shortAspect ?nameaspect ?pointdefs ?list ?pointcut]
       (equals ?advicesize (count  (ekeko [?aspect ?advice ?pointcut] (NOAdvices ?aspect ?advice ?pointcut))))
       (equals ?advancesize (count (ekeko [?shortAspect ?nameaspect ?pointdefs ?list] (NOAAdvice ?shortAspect ?nameaspect ?pointdefs ?list))))
       (equals ?total (- ?advicesize ?advancesize))))
 
 (ekeko [?size ] (NOBAdvices ?size))
 
 ;############################### InheritedAspects : Are aspects often inherited by abstract aspects? ############################### 
 (defn InheritedAspets [?aspect ?super]
   (l/fresh  [?source]
             (NOAspects ?aspect ?source)
             (w/aspect-declaredsuper ?aspect ?super)
             (succeeds (.isAbstract ?super))))
 
 (inspect (sort-by first (ekeko [?aspectname ?abstract] (l/fresh[?aspect] (InheritedAspets ?aspect ?abstract) (equals ?aspectname (.getName ?aspect))))))
 (count (ekeko [?aspectname ?name] (InheritedAspets ?aspectname ?name)))
 
 ;############################### SA: Number of Singleton Aspects? ###############################
 (defn NOSingletonAspects [?aspect ?association]
   "the number of singleton aspects"
   (l/fresh  [?source]
             (NOAspects ?aspect ?source)
             (equals ?association (.getName (.getKind (.getPerClause (.getDelegate ?aspect)))))
             (succeeds (= "issingleton" ?association))))
  
 (inspect (ekeko [?a ?d] (NOSingletonAspects ?a ?d)))
 (count (ekeko [?a ?d] (NOSingletonAspects ?a ?d))) 
 
 ;############################### nSA: Number of non-Singleton Aspects? ###############################
 (defn NOnonSAspects [?aspect ?association]
   (l/fresh  [?source]
             (NOAspects ?aspect ?source)
             (equals ?association (.getName (.getKind (.getPerClause (.getDelegate ?aspect)))))
             (equals false (= "issingleton" ?association))))
 
 (inspect (ekeko [?a ?d] (NOnonSAspects ?a ?d)))
 (count (ekeko [?a ?d] (NOnonSAspects ?a ?d)))
  
 ;###############################* AE: How often is adviceExecution() used? ###############################
 (defn NOAdviceexecution [?shortAspect ?advicekind ?pointcut ?arg]
   (l/fresh [?aspect ?advice ?args ?ar]
            (NOAdvices ?aspect ?advice ?pointcut)
            (equals ?shortAspect (str "Aspect {"(.getSimpleName ?aspect)"}"))
            (equals ?advicekind (str "Advice {"(.toString (.getKind ?advice))"}"))
            (getAdvance ?pointcut ?args)
            (equals ?ar (vec (into #{} ?args)))
            (contains ?ar ?arg)
            (succeeds (and (= (.getPointcutKind ?arg) 1) 
                           (= "adviceexecution" (.toString (getKind ?arg)))))))
 
 (inspect  (sort-by first (ekeko [?shortAspect ?nameaspect ?list ?pointdefs] (NOAdviceexecution ?shortAspect ?nameaspect ?pointdefs ?list)))) 
 (count (ekeko [?shortAspect ?nameaspect ?list ?pointdefs]  (NOAdviceexecution ?shortAspect ?nameaspect ?pointdefs ?list)))
 
 ;############################### AJPS: How many join point shadows per advice? Calculate the average of them! ###############################
	(defn- Advice-Shadow [?adviceN ?shadow]
	  (l/fresh[?aspect ?advice ?pointcut]
	          (NOAdvices ?aspect ?advice ?pointcut)
	          (w/advice-shadow ?advice ?shadow)
	          (equals ?adviceN (.getName (.getSignature ?advice)))))
  
	(defn- countShadowsSize [?advicename]
	  (count (ekeko [?advice ?shadow]
	         (Advice-Shadow ?advice ?shadow)
	         (equals true (= ?advicename ?advice))))) 
 
 ;visual representation!
 (defn JPShadows-perAdvice [?aspectname ?adviceKind ?shadowSize]
   (l/fresh [?aspect ?advice ?advicename ?pointcut]
            (NOAdvices ?aspect ?advice ?pointcut)
            (equals ?advicename (.getName (.getSignature ?advice)))
            (equals ?shadowSize (countShadowsSize ?advicename))
            (equals ?aspectname (str "Aspect {"(.getSimpleName ?aspect)"}"))
            (equals ?adviceKind (str "Advice {"(.getKind ?advice)"}"))))
  
  (inspect (sort-by first (ekeko [?a ?ad ?s] (JPShadows-perAdvice ?a ?ad ?s))))
  
  ;Calculate the average of jps -> total number of jps divided by the number of advices declared in a system.
  (defn- NOJPS [?shadowSize]
    (l/fresh [?aspect ?advice ?advicename ?pointcut]
             (NOAdvices ?aspect ?advice ?pointcut)
             (equals ?advicename (.getName (.getSignature ?advice)))
             (equals ?shadowSize (countShadowsSize ?advicename))))
  ;point 
  (defn- sizeOfJPS []
    (ekeko [?sizes] (NOJPS ?sizes)))

  (defn- newCollection [list res]
    (doseq [x list] (.add res (first x))))
  
  (defn- listOfJPShadows [?arglist]
   (l/fresh [?result ?y]
     (equals ?result (java.util.ArrayList. []))
     (equals ?y (newCollection (sizeOfJPS) ?result))
     (equals ?arglist ?result)))
  
  (defn- collectionJPS []
    (first (first (ekeko [?A] (listOfJPShadows ?A)))))
  ;get the sequence of jps numbers : [1 1 1 1 2 1 1 0 4 11 1 21 1 1 3 2 4 1 1 1 1 1 1 1 3 1 1 1 1 1 15 71 19 26 1 0 1 1 1 0 1 1 0 19 1 145 2 7 1 4 0 0 19 1 1 1 0 1 1 35 7 1 14 1 14 14 1 1 1 1 7 18 18 19]
  (collectionJPS)
  
  (defn- calculateJPS []
    (reduce + (collectionJPS)))
  
  ;total average! AJPS:
  (defn AJPShadows []
    (format "%.2f" (float (/ (calculateJPS) (COUNTNOAdvices)))))
  (AJPShadows)
   
 ;###############################* NOAG: How many args() are bound ? Calculate the average of args() pointcut.. ###############################
 (defn- PPargs [pointcut res]
   "a nested function \"PPargs\" is to pick up the related primitive pointcut that its kind ID are 4!"
   (if  (or (= (getPKind pointcut) 5 ) (= (getPKind pointcut) 6))
      [(PPargs (getLeft pointcut) res) (PPargs (getRight pointcut) res)]
      (if (= (getPKind pointcut) 4) (addargs res pointcut))))
 
 (defn- getArgs [?pointcut ?arglist]
   "it is responsible for the primitive pointcut called \"getArgs\" "
   (l/fresh [?result ?y]
     (equals ?result (java.util.ArrayList. []))
     (equals ?y (PPargs ?pointcut ?result))
     (equals ?arglist ?result)))
 
 ;search in each pointcut for an args() pointcut
  (defn NOArgs [?shortAspect ?advicekind ?arg ?return ?size]
    (l/fresh [?aspect ?advice ?args ?pointcut ?sootname ?sizeR ?soot|method ?sizelist]
             (NOAdvices ?aspect ?advice ?pointcut)
             (equals ?shortAspect (str "Aspect {"(.getSimpleName ?aspect)"}"))
             (equals ?advicekind (str "Advice {"(.toString (.getKind ?advice))"}"))
             (getArgs ?pointcut ?args)
             (equals false (empty? ?args))
             (equals ?arg (vec (into #{} ?args)))
             (equals ?return (collectArguments ?arg))
             (equals ?sizelist (java.util.ArrayList. []))
             (equals ?sizeR (argumentsize (clojure.string/split ?return  #",") ?sizelist))
             (equals ?size (count ?sizelist))))
  
  (inspect  (sort-by first (ekeko [?shortAspect ?advicekind ?list ?r ?size] (NOArgs ?shortAspect ?advicekind ?list ?r ?size))))
  
  (defn- argumentsize [list size]
    (doseq [x list :when (not (empty? x))] (.add size "1")))
  
  (defn- AmountOfArgs-perAdvice [?size]
    (l/fresh [?shortAspect ?advicekind ?list ?return ]
             (NOArgs ?shortAspect ?advicekind ?list ?return ?size)))
  
  (defn- collectionOfArgs []
    (ekeko [?a] (AmountOfArgs-perAdvice ?a)))
  
  (defn- newCollectionOfArgs [list res]
    (doseq [x list] (.add res (first x))))
  
  (defn- listOfArgs [?arglist]
   (l/fresh [?result ?y]
     (equals ?result (java.util.ArrayList. []))
     (equals ?y (newCollectionOfArgs (collectionOfArgs) ?result))
     (equals ?arglist ?result)))
  
  (defn- seqARG []
    (first (first (ekeko [?A] (listOfArgs ?A)))))
  ;get the sequence of ARGS numbers : [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 2 0 0 1 1 0 0 0 0 1 1 0 1 0 0 0 1 0 0 0 0 0 0 0 1 1 0 0 0 0 0 1 0 1 0 0 0 0 0]
  (seqARG)
  
  (defn- calculateARGS []
    (reduce + (seqARG)))
  
  ;total average! args():
  (defn AvARGS []
    (format "%.2f" (float (/ (calculateARGS) (COUNTNOAdvices)))))
  (AvARGS)
  
  ;total average! args():
  (defn AvARGS-v1 []
    (format "%.2f" (float (/ (calculateARGS) (count (ekeko [?shortAspect ?advicekind ?list ?r ?size] (NOArgs ?shortAspect ?advicekind ?list ?r ?size)))))))
  (AvARGS-v1)
  
 ;############################### NOnPC: In how many around advice is it possible to (not) execute a proceed call? ###############################
  (defn NOPC [?aspectName ?soot|methodName]
          (l/fresh [?aspect ?adv ?unit ?soot|advicemethod ?units ?pointcut]
            (NOAdvices ?aspect ?adv ?pointcut)
            (ajsoot/advice-soot|method ?adv ?soot|advicemethod)
            (succeeds (.hasActiveBody ?soot|advicemethod))
            (succeeds (.startsWith (.getName ?soot|advicemethod) "ajc$around$"))
            (soot|unit-getDeclarationClassname ?soot|advicemethod ?aspectName)
            (equals ?units (.getUnits (.getActiveBody ?soot|advicemethod)))
            (equals ?soot|methodName (str "Method {"(.getName ?soot|advicemethod)"}"))
            (contains ?units ?unit)
            (succeeds (.containsInvokeExpr ?unit))
	          (succeeds (or (= "soot.jimple.internal.JAssignStmt" (.getName (.getClass ?unit)))  
	                        (= "soot.jimple.internal.JInvokeStmt" (.getName (.getClass ?unit)))))
            (succeeds (and 
                        (.startsWith (.getName (.getMethod (.getValue (.getInvokeExprBox ?unit)))) "ajc")
                        (lastIndexOfText (.getName (.getMethod (.getValue (.getInvokeExprBox ?unit)))) "proceed")))))
 
 (inspect (sort-by first (ekeko [?a ?s] (NOPC ?a ?s))))
 (count (ekeko [?a ?s] (NOPC ?a ?s)))
 
 ;COUNT AROUND ADVIces THAT HAVE NO proceed CALL
 (defn NOnPC []
    (- (NOAround) (count (ekeko [?a ?s] (NOPC ?a ?s)))))
 (NOnPC)
 
 ;############################### How many before/after advice VS around advice VS after Throwing/Returning advice? ###############################
 ;NOAround Advice
 (defn NOAround []
   "count the number around advices in a system"
   (count (ekeko [?aspect ?advice ?pointcut] 
                 (NOAdvices ?aspect ?advice ?pointcut)
                 (succeeds (= (.getName (.getKind ?advice)) "around")))))
 (NOAround)
 
 (defn NOAfter []
   "count the number after advices in a system"
   (count (ekeko [?aspect ?advice ?pointcut] 
                 (NOAdvices ?aspect ?advice ?pointcut)
                 (succeeds (= (.getName (.getKind ?advice)) "after")))))
 (NOAfter)
 
 (defn NOBefore []
   "count the number before advices in a system"
   (count (ekeko [?aspect ?advice ?pointcut]
                 (NOAdvices ?aspect ?advice ?pointcut)
                 (succeeds (= (.getName (.getKind ?advice)) "before")))))
 (NOBefore)

 ;Number of Before/After advices
 (defn NOBAAdvices []
   "count the number before and after advices in a system"
   (+ (NOAfter) (NOBefore)))
 
 (NOBAAdvices)
 
 ;Number of after returning
 (defn NOAReturning []
    "count the number before and after returning advice in a system"
   (count (ekeko [?aspect ?advice ?pointcut] 
                 (NOAdvices ?aspect ?advice ?pointcut)
                 (succeeds (= (.getName (.getKind ?advice)) "afterReturning")))))
 (NOAReturning)
 ;Number of after throwing
 (defn NOAThrowing []
    "count the number after throwing advice in a system"
   (count (ekeko [?aspect ?advice ?pointcut] 
                 (NOAdvices ?aspect ?advice ?pointcut)
                 (succeeds (= (.getName (.getKind ?advice)) "afterThrowing")))))
 (NOAThrowing)
 
 ;Total number of After throwing/returning advices
 (defn NOAfterTRAdvices []
   "count the number after throwing and returning advices in a system"
   (+  (NOAThrowing)  (NOAReturning)))
 
 (NOAfterTRAdvices)
 
 ;############################### AdC: Calculate the average of advised classes in a system? ###############################
 ;looking at all kinded pointcuts in order to collect all advised classes
 (defn- NOClasses-AdC1 [?classname ?isSubClass]
   "collect all the advised classes -method/constructor execution" 
   (l/fresh [?aspect ?advice ?shadow ?class ?from ?pointcut]
            (NOAdvices ?aspect ?advice ?pointcut)
            (w/advice-shadow ?advice ?shadow)
            (equals ?class (.getParent ?shadow))
            (succeeds (= "class" (.toString (.getKind ?class))))
            (equals ?classname (str (.getPackageName (.getParent ?shadow))"."(.getName (.getParent ?shadow))))
            (equals ?from (isClass ?classname))
            (equals false (nil? ?from))
            (equals ?isSubClass (first ?from))))
 
 (inspect (ekeko [?c ?i] (NOClasses-AdC1 ?c ?i)))
  
 (defn- NOClasses-AdC2 [?classname ?isSubClass]
   "collect all the advised classes -method/constructor call & field-get so on.."
   (l/fresh [?aspect ?advice ?shadow ?class ?from ?pointcut]
           (NOAdvices ?aspect ?advice ?pointcut)
           (w/advice-shadow ?advice ?shadow)
           (equals ?class (.getParent (.getParent ?shadow)))
           (succeeds (= "class" (.toString (.getKind ?class))))
           (equals ?classname (str (.getPackageName (.getParent (.getParent ?shadow)))"."(.getName (.getParent (.getParent ?shadow)))))
           (equals ?from (isClass ?classname))
           (equals false (nil? ?from))
           (equals ?isSubClass (first ?from))))
 
 (inspect (ekeko [?C ?i] (NOClasses-AdC2 ?C ?i)))
 
 (defn getAdvisedClasses []
   "combine the above two functions -boolean true value refers to the related class which is a subclass"
   (distinct
     (clojure.set/union
       (ekeko [?advisedc ?isSubClass] (NOClasses-AdC1 ?advisedc ?isSubClass))
       (ekeko [?advisedc ?isSubClass] (NOClasses-AdC2 ?advisedc ?isSubClass)))))
 
 (inspect (getAdvisedClasses))
 (count (getAdvisedClasses))
 
 ;AVERAGE of the advised Classes
 (defn AverageAdvisedClasses []
   (format "%.2f" (float (/ (count (getAdvisedClasses)) (CountNOClasses)))))
 
 (AverageAdvisedClasses)
 
 (defn- isClass [?name]
   (first
     (ekeko [?isSub]
            (l/fresh [?cn ?classes]
                     (NOClasses ?classes)
                     (equals ?cn (.getName ?classes))
                     (equals true (= ?cn ?name))
                     (equals ?isSub (false? (= "java.lang.Object" (.getName (.getSuperclass ?classes)))))))))
 
 ;############################### NOC VS NOE : How often is call pointcut used VS execution pointcut in an entire system? ###############################
 ;method/construct call VS method/construct execution 
 (defn- CallsExecutions [?shortAspect ?advicekind  ?callexecution  ?pointdefs] 
   (l/fresh [?aspect ?advice ?primPoint ?ar]
            (NOAdvices ?aspect ?advice ?pointdefs)
            (equals ?shortAspect (str "Aspect {"(.getSimpleName ?aspect)"}"))
            (equals ?advicekind (str "Advice {"(.toString (.getKind ?advice))"}"))
            (getRelatedKinds ?pointdefs ?primPoint)
            (equals ?ar (vec (into #{} ?primPoint)))
            (equals false (empty? ?ar))
            (contains ?ar ?callexecution)))
 
 (inspect  (sort-by first (ekeko [?shortAspect  ?advice ?list ?callexe] (CallsExecutions ?shortAspect ?advice ?list ?callexe))))
 (count (ekeko [?shortAspect ?advice ?list ?callexe] (CallsExecutions ?shortAspect ?advice  ?list ?callexe)))
 
 ;Number of CALL pointcut
 (defn NOCall [?shortAspect ?call ?advicekind]
   (l/fresh [ ?pointdefs]
            (CallsExecutions ?shortAspect ?advicekind  ?call  ?pointdefs)
            (succeeds (or (= "method-call" (.toString (getKind ?call))) 
                          (= "constructor-call" (.toString (getKind ?call)))))))
 
 (inspect (sort-by first (ekeko [?a ?c ?ad] (NOCall ?a ?c ?ad))))
 (count (ekeko [?a ?c ?ad] (NOCall ?a ?c ?ad)))
 
 ;Number of EXECUTION pointcut
 (defn NOExecution [?shortAspect ?execution ?advicekind]
   (l/fresh [?pointdefs]
            (CallsExecutions ?shortAspect ?advicekind  ?execution  ?pointdefs)
            (succeeds (or (= "method-execution" (.toString (getKind ?execution))) 
                          (= "constructor-execution" (.toString (getKind ?execution)))))))
 
 (inspect (sort-by first (ekeko [?a ?e ?ad] (NOExecution ?a ?e ?ad))))
 (count (ekeko [?a ?e ?ad] (NOExecution ?a ?e ?ad)))
  
 (defn- getRelatedKinds [?pointcut ?list]
   "it is responsible for some primitive pointcuts that reach certain modules 
    such as method-call/execution, construct-call/execution, field-get/set"
   (l/fresh [?res ?y]
     (equals ?res (java.util.ArrayList. []))
     (equals ?y (calls ?pointcut ?res))
     (equals ?list ?res)))
 
 (defn- calls [pointcut res]
   "a nested function \"calls\" is to pick up the related poincuts whose pointcut kind ID are 1!"
   (if  (or (= (getPKind pointcut) 5 ) (= (getPKind pointcut) 6))
      [(calls (getLeft pointcut) res) (calls (getRight pointcut) res)]
      (if (and (= (getPKind pointcut) 1) (or (= "field-set" (.getName (getKind pointcut))) 
                                             (= "field-get" (.getName (getKind pointcut)))
                                             (= "method-call" (.getName (getKind pointcut)))
                                             (= "method-execution" (.getName (getKind pointcut)))
                                             (= "constructor-call" (.getName (getKind pointcut)))
                                             (= "constructor-execution" (.getName (getKind pointcut)))))
        (addlist res pointcut))))
 
 ;############################### nAdC: Calculate the average of non-advised classes in a system? ###############################
 ;Which parts of the system are advised? 
 (defn- NOAClasses [?advised]
                  (l/fresh [?gets ?gete]
                           (equals ?gets (getAdvisedClasses));line 856
                           (contains ?gets ?gete)
                           (equals ?advised (first ?gete))))
 (inspect (ekeko [?ac] (NOAClasses ?ac)))
 (count (ekeko [?ac] (NOAClasses ?ac)))
 
 ;(not) advised 
 (inspect (clojure.set/difference 
            (set (ekeko [?classes] (l/fresh [?c]  (NOClasses ?c) (equals ?classes (.getName ?c))))) 
            (set (ekeko [?ac] (NOAClasses ?ac)))))
 
 (defn countnonAdvised []
   (count (clojure.set/difference 
            (set (ekeko [?classes] (l/fresh [?c]  (NOClasses ?c) (equals ?classes (.getName ?c))))) 
            (set (ekeko [?ac] (NOAClasses ?ac))))))
 
 ;AVERAGE of the non-advised Classes
 (defn AverageNonAdvisedClasses []
   (format "%.2f" (float (/ (countnonAdvised) (CountNOClasses)))))
 
 (AverageNonAdvisedClasses)
 
 ;############################### Amount of non-advised VS advised methods of classes ###############################
 (defn showMethods-exe []
   "Simple get the all method-executions from shadows"
  (sort-by first (lazy-seq (into #{} (ekeko [?methodname]
                                            (l/fresh [?methodcut ?classname ?advice ?pointcut ?aspect ?class ?from ?shadow]
                                                     (NOAdvices ?aspect ?advice ?pointcut)
                                                     (w/advice-shadow ?advice ?shadow)
                                                     (equals ?class (.getParent ?shadow))
                                                     (succeeds (= "class" (.toString (.getKind ?class))))
                                                     (equals ?classname (str (.getPackageName ?class)"."(.getName ?class)))
                                                     (equals ?from (isClass ?classname))
                                                     (equals false (nil? ?from))
                                                     (equals ?methodcut (str (.getPackageName ?class)"."(.getName ?class)"."(.toString ?shadow)))
                                                     (equals ?methodname (splitmethod (clojure.string/split ?methodcut  #",")))))))))
  ;(equals false (nil? (isMethod ?methodname)))))))))
 
 (defn- splitmethod [name]
   (if (not (empty? name)) (str (first name)(if (not (empty? (rest name))) (str ", "(splitmethod (rest name))))) (str name)))
 
 (inspect (showMethods-exe))
 
 (defn showMethods-call []
   "Simple get the all method-calls from shadows"
   (lazy-seq (into #{} (ekeko [?methodclassname ?methodname]
                              (l/fresh [?shadow ?classname ?methodstr ?advice ?pointcut ?aspect ?class ?from]
                                       (NOAdvices ?aspect ?advice ?pointcut)
                                       (w/advice-shadow ?advice ?shadow)
                                       (equals ?class (.getParent (.getParent ?shadow)))
                                       (succeeds (= "class" (.toString (.getKind ?class))))
                                       (equals ?classname (str (.getPackageName ?class)"."(.getName ?class)))
                                       (equals ?from (isClass ?classname))
                                       (equals false (nil? ?from))
                                       (succeeds (.startsWith (.getName ?shadow) "method-call"))
                                       (equals ?methodstr (str (subs (.getName ?shadow) (+ (.indexOf (.getName ?shadow) " ") 1))))
                                       (equals ?methodname (subs ?methodstr 0 (.lastIndexOf ?methodstr ")")))
                                       (equals ?methodclassname (subs (subs ?methodname 0 (.indexOf ?methodname "(")) 0 (.lastIndexOf (subs ?methodname 0 (.indexOf ?methodname "(")) "."))))))))
                                       
 (inspect (sort-by first (showMethods-call)))
 
 (defn showMethods-calls []
   (ekeko [?methodn]
          (l/fresh [?list ?item ?classn ]
                   (equals ?list (showMethods-call))
                   (contains ?list ?item)
                   (equals ?classn (first ?item))
                   (equals ?methodn (first (rest ?item)))
                   (succeeds (nil?  (getInterface ?classn))))))
 
 (inspect (sort-by first (showMethods-calls)))
 
 (defn getMethodsFromShadow []
   "combine the above two functions"
   (sort-by first (distinct (clojure.set/union (showMethods-exe) (showMethods-calls)))))
 
 (inspect  (getMethodsFromShadow))
 
 (defn- allMethods []
   (sort-by first (ekeko [?mn]
                         (l/fresh [?types ?list ?m]
                                  (NOM ?types ?m)
                                  (equals ?mn (subs (.toString ?m) (+ (.indexOf (.toString ?m) " ") 1 )))))))
 (inspect (allMethods))
 (count (allMethods))
 ;THE NUMBER OF ADVISED METHODS
 (defn Collection|AdvisedMethods []
   (sort-by first  (clojure.set/intersection 
                     (set (getMethodsFromShadow)) 
                     (set (allMethods)))))
 
 (inspect (Collection|AdvisedMethods))
 (count (Collection|AdvisedMethods))
 
  ;THE NUMBER OF non-ADVISED METHODS
 (defn Collection|NonAdvisedMethods []
   (- (count (allMethods)) (count (Collection|AdvisedMethods))))
 
 (Collection|NonAdvisedMethods)
 
 ;############################### CsC: Do aspects often advise classes with a lot of subclasses? ###############################
 ;simply counts the number of classes along with their subclasses | calculate the everage of them both!
 (defn- declaringTypesMethodConstruct []
   (lazy-seq (into #{} (ekeko [?calltype]
                              (l/fresh [ ?pointdefs  ?call   ?shortAspect ?advicekind]
                                       (CallsExecutions ?shortAspect ?advicekind  ?call  ?pointdefs)
                                       (succeeds (or (= "method-call" (.toString (getKind ?call))) (= "method-execution" (.toString (getKind ?call)))))
                                       (equals ?calltype  (.getDeclaringType (.getSignature ?call)))
                                       (equals false (= "org.aspectj.weaver.patterns.WildTypePattern" (.getName (.getClass ?calltype))))
                                       (equals false (or 
                                                       (IndexOfText (.toString ?calltype) "+")
                                                       (IndexOfText (.toString ?calltype) "*")
                                                       (IndexOfText (.toString ?calltype) ".."))))))))
 (inspect (declaringTypesMethodConstruct))
 ;Bank.getAccounts();  -> Bank -> KBC & Fortis
 
 ;Main Query!
 (defn CsC [?class ?listofsubclasses]
   (l/fresh [?type ?types ?subclassofclass]
            (equals ?types (declaringTypesMethodConstruct))
            (contains ?types ?type)
            (equals ?class (.getName (.getType (first ?type))))
            (succeeds  (nil? (getInterface ?class)))
            (equals ?subclassofclass (class|subclasses ?class))
            (getsubclasses ?subclassofclass ?listofsubclasses)))
 
 (inspect (ekeko [?i ?s] (CsC ?i ?s)))
 (count (ekeko [?i ?s] (CsC ?i ?s)))
 
 (defn- getsubclasses [?class ?list]
   (l/fresh [?res ?y]
            (equals ?res (java.util.ArrayList. []))
            (equals ?y (metric ?class ?res))
            (equals ?list ?res)))
 
 (defn- metric [class list]
   (if (not (empty? class))
     [(addlist list (first class)) (metric (class|subclasses (first (first class))) list) (metric (rest class) list)]))
 
 (defn- class|subclasses [?isSameSuperClass]
   "return classes (subclasses) that extend to \"isSameSuperClass\""
   (ekeko [?subclasses]
          (l/fresh [?class ?super]
                   (NOClasses ?class)
                   (equals ?subclasses (.getName ?class))
                   (equals ?super (.getName (.getSuperclass ?class)))
                   (equals false (= "java.lang.Object" ?super))
                   (equals true (= ?isSameSuperClass ?super)))))
 
 (inspect (class|superclass))
 
 ;Count the number of classes that have at least one subclass -->
 (defn NOCsC [?class ?subs]
   (l/fresh []
            (CsC ?class ?subs)
            (succeeds (false? (empty? ?subs)))))
 
 (inspect (ekeko [?c ?s] (NOCsC ?c ?s)))
 (count (ekeko [?c ?s] (NOCsC ?c ?s)))
 
 ;the average of the classes with their subclasses -->
  (defn collectionCsC []
   (ekeko [?size]
          (l/fresh [?class ?subs]
                   (CsC ?class ?subs)
                   (equals ?size (count ?subs)))))
 
 (inspect (collectionCsC ))
  
 (defn- newCollectionOfCsC [list res]
   (doseq [x list] (.add res (first x))))
  
  (defn- listOfCsC [?arglist]
   (l/fresh [?result ?y]
     (equals ?result (java.util.ArrayList. []))
     (equals ?y (newCollectionOfCsC (collectionCsC) ?result))
     (equals ?arglist ?result)))
  
  (defn- seqCsC []
    (first (first (ekeko [?A] (listOfCsC ?A)))))
  (seqCsC)
  
  (defn- calculateACsC []
    (reduce + (seqCsC)))
  
  ;total average! ScC():
  (defn AvScC []
    (format "%.2f" (float (/ (calculateACsC) (count (collectionCsC))))))
  (AvScC)
 
 ;############################### Wildcards: How often are wildcards used in modules declared in method/construct-call/execution and field-get/set? ###############################
 (defn- functionNOW [?shortAspect ?itemName ?advicekind ?decType ?excTypeName]
   (l/fresh [?callexecution  ?pointcuts ?aspect ?advice ?primPoint ?ar]
            (NOAdvices ?aspect ?advice ?pointcuts)
            (equals ?shortAspect (str "Aspect {"(.getSimpleName ?aspect)"}"))
            (equals ?advicekind (str "Advice {"(.toString (.getKind ?advice))"}"))
            (getCollectionsOfPointcut ?pointcuts ?primPoint)
            (equals ?ar (vec (into #{} ?primPoint)))
            (equals false (empty? ?ar))
            (contains ?ar ?callexecution)
            (equals ?itemName (str  (.getKind ?callexecution)"{ "(.toString (.getSignature ?callexecution))" }"))
            (equals ?decType (.getDeclaringType (.getSignature ?callexecution)))
            (equals ?excTypeName (.getExactType ?decType))))
 
 (inspect (sort-by first (ekeko [?shortAspect ?itemName ?advicekind ?decType ?excTypeName] (functionNOW ?shortAspect ?itemName ?advicekind ?decType ?excTypeName))))
 (count (ekeko [?shortAspect ?itemName ?advicekind ?decType ?excTypeName] (functionNOW ?shortAspect ?itemName ?advicekind ?decType ?excTypeName)))
 
 ;Main 
 (defn NOWilcards []
   (count (ekeko[?shortAspect ?itemName ?advicekind ?decType ?excTypeName]
                (functionNOW ?shortAspect ?itemName ?advicekind ?decType ?excTypeName)
                (succeeds (or 
                            (IndexOfText (.toString ?decType) "+") ;check all wildcards!
                            (IndexOfText (.toString ?decType) "*")
                            (IndexOfText (.toString ?decType) ".."))))))
 ;METRIC 
 (NOWilcards)
 
 ;############################### NOnW: How often are wildcards (not) used in modules declared in method/construct-call/execution and field-get/set? ###############################
 ;Main 
 (defn non-Wilcards []
   (count (ekeko[?shortAspect ?itemName ?advicekind ?decType ?excTypeName]
                (functionNOW ?shortAspect ?itemName ?advicekind ?decType ?excTypeName)
                (equals false (or 
                                (IndexOfText (.toString ?decType) "+") ;check all wildcards!
                                (IndexOfText (.toString ?decType) "*")
                                (IndexOfText (.toString ?decType) "..")))
                (succeeds (nil? (getInterface (.toString ?excTypeName)))))))
 ;METRIC 
 (non-Wilcards) 
 ;--------------------------------------------------------------------------
  (defn- getCollectionsOfPointcut [?pointcut ?list]
   "it is responsible for some pointcuts that reach certain modules "
   (l/fresh [?res ?y]
     (equals ?res (java.util.ArrayList. []))
     (equals ?y (findpointcuts ?pointcut ?res))
     (equals ?list ?res)))
 
 (defn- findpointcuts [pointcut res]
   "a nested function \"findpointcuts\" is to pick up the related poincuts. Basicly, in order to collect all used wildcards, 
     it must be looked at the specific pointcuts such as  method-call, method-execution, constructor-call, constructor-execution, field-get, and field-set"
   (if  (or (= (getPKind pointcut) 5 ) (= (getPKind pointcut) 6))
     [(findpointcuts (getLeft pointcut) res) (findpointcuts (getRight pointcut) res)]
     (if (and (= (getPKind pointcut) 1) (or (= "field-set" (.getName (getKind pointcut))) 
                                            (= "field-get" (.getName (getKind pointcut)))
                                            (= "method-call" (.getName (getKind pointcut)))
                                            (= "method-execution" (.getName (getKind pointcut)))
                                            (= "constructor-call" (.getName (getKind pointcut)))
                                            (= "constructor-execution" (.getName (getKind pointcut)))))
       (addlist res pointcut)
       (if (= (getPKind pointcut) 7);check also "not pointcuts"!
         (if (and (= (getPKind (.getNegatedPointcut pointcut)) 1) 
                  (or (= "field-set" (.getName (getKind (.getNegatedPointcut pointcut)))) 
                      (= "field-get" (.getName (getKind (.getNegatedPointcut pointcut))))
                      (= "method-call" (.getName (getKind (.getNegatedPointcut pointcut))))
                      (= "method-execution" (.getName (getKind (.getNegatedPointcut pointcut))))
                      (= "constructor-call" (.getName (getKind (.getNegatedPointcut pointcut))))
                      (= "constructor-execution" (.getName (getKind (.getNegatedPointcut pointcut))))))
           (addlist res (.getNegatedPointcut pointcut)))))))
 
 ;############################### TJPS: How often are thisJoinPoint/thisJoinPointStatic used in a given AspectJ project? ###############################
 ;count number of thisJoinPoints that are used at least once per advice body!
 ;count the number of advices that thisJoinPoint and thisJoinPointStatic are used at least once in the body of the advices .
 (defn NOJPS [?DC|advicemethod ?sootname ?soot|advicemethod]
   (l/fresh [?adv ?aspect ?units ?unit ?pointcut]
            (NOAdvices ?aspect ?adv ?pointcut)
            (ajsoot/advice-soot|method ?adv ?soot|advicemethod)
            (succeeds (.hasActiveBody ?soot|advicemethod))
            (equals ?units (.getUnits (.getActiveBody ?soot|advicemethod)))
            (contains ?units ?unit)
            (succeeds (= "soot.jimple.internal.JIdentityStmt" (.getName (.getClass ?unit))))
            (succeeds (or 
                        (= "org.aspectj.lang.JoinPoint" (.toString (.getType (.getValue (.getRightOpBox  ?unit)))))
                        (= "org.aspectj.lang.JoinPoint$StaticPart" (.toString (.getType (.getValue (.getRightOpBox  ?unit)))))))
            (equals ?sootname (.getName ?soot|advicemethod))
            (equals ?DC|advicemethod (.getName (.getDeclaringClass ?soot|advicemethod)))))
 
 (inspect (sort-by first (into #{} (ekeko [?a ?b ?s] (NOJPS ?a ?b ?s)))))
 (count (into #{} (ekeko [?a ?b ?s] (NOJPS ?a ?b ?s))))
 
 ;############################### Args : How often are args() only accessed? How often are they modified?  ###############################
 ; amount of arguments accessed and modified in the body of advices 
 (defn NOAccessModifyArgs  [?sootname ?return ?parameters ?arg ?unit ?istrue ?soot|method]
   (l/fresh [?units ?aspect ?advice ?pointcut ?args ?ret ?getbool]
            (NOAdvices ?aspect ?advice ?pointcut)
            (ajsoot/advice-soot|method ?advice ?soot|method)
            (succeeds (= (.getName (.getSignature ?advice)) (.getName ?soot|method)))
            ;(equals true (= "OptimizedAbstractTimestampAspect" (.getSimpleName ?aspect)))
            (getArgs ?pointcut ?args)
            (equals false (empty? ?args))
            (equals ?arg (vec (into #{} ?args)))
            (equals ?sootname  (.getName ?soot|method))
            (equals ?return (collectArguments ?arg))
            (equals ?units (.getUnits (.getActiveBody ?soot|method)))
            (localArgs ?return ?units ?parameters);?list represents  the soot version of the arguments that refers to the declared args()!
            (contains ?units ?unit)
            (equals false (= "soot.jimple.internal.JIdentityStmt" (.getName (.getClass ?unit))))
            (equals ?istrue (java.util.ArrayList. []))
            (equals ?getbool (isAccessedOrModified ?parameters  ?unit ?istrue))
            (succeeds (false? (empty? ?istrue)))))
 
 (inspect (sort-by first (ekeko [?sootname ?return ?parameters ?arg ?unit ?istrue ?soot|method] (NOAccessModifyArgs ?sootname ?return ?parameters ?arg ?unit ?istrue ?soot|method))))
 (count (ekeko [?sootname ?return ?parameters ?arg ?unit ?istrue ?soot|method] (NOAccessModifyArgs ?sootname ?return ?parameters ?arg ?unit ?istrue ?soot|method)))

  ;Get the number of modified arguments of declared args pointcuts.
 (defn NOModifying [?return ?parameters ?arg ?unit ?istrue]
   (l/fresh [?sootname ?soot|method]
          (NOAccessModifyArgs ?sootname ?return ?parameters ?arg ?unit ?istrue ?soot|method)
          (equals false (nil? (first (modifyARG  ?istrue))))))
 
 (inspect  (sort-by first (ekeko [?return ?parameters ?arg ?unit ?istrue] (NOModifying ?return ?parameters ?arg ?unit ?istrue))))
 (count (ekeko [?return ?parameters ?arg ?unit ?istrue] (NOModifying ?return ?parameters ?arg ?unit ?istrue)))
 
 (defn- NOAccess [?return ?parameters ?arg ?unit ?istrue ?numbers]
   (l/fresh [?sootname  ?soot|method]
            (NOAccessModifyArgs ?sootname ?return ?parameters ?arg ?unit ?istrue ?soot|method)
            (equals false (nil? (first (accessARG  ?istrue))))
            (equals ?numbers (count ?istrue))))
 
 (inspect  (sort-by first (ekeko [?return ?parameters ?arg ?unit ?istrue ?numbers] (NOAccess ?return ?parameters ?arg ?unit ?istrue ?numbers))))
 (count(ekeko [?return ?parameters ?arg ?unit ?istrue ?numbers] (NOAccess ?return ?parameters ?arg ?unit ?istrue ?numbers)))
 
 (defn- AmountOfaccessingArgs [?numbers]
   (l/fresh [?return ?parameters ?arg ?unit ?istrue ]
            (NOAccess ?return ?parameters ?arg ?unit ?istrue ?numbers)))
  
  (defn- collectionOfAccess []
    (ekeko [?a] (AmountOfaccessingArgs ?a)))
  
  (defn- newCollectionOfaccessingargs [list res]
    (doseq [x list] (.add res (first x))))
  
  (defn- listOfAcArgs [?arglist]
   (l/fresh [?result ?y]
     (equals ?result (java.util.ArrayList. []))
     (equals ?y (newCollectionOfaccessingargs (collectionOfAccess) ?result))
     (equals ?arglist ?result)))
  
  (defn- seqAcARG []
    (first (first (ekeko [?A] (listOfAcArgs ?A)))))
  (seqAcARG)
  
  ;Get the number of accessed arguments of declared args pointcuts.
  (defn NumberOfAccessedARGS []
    (reduce + (seqAcARG)))
  
  (NumberOfAccessedARGS)
;)
 
 (defn modifyARG [list]
   (for [x list :when (= "modify" x)] (str "true")))
 
 (defn accessARG [list]
   (for [x list :when (= "access" x)] (str "true")))
 
 (defn isAccessedOrModified [list unit collec]
   (if (= "soot.jimple.internal.JAssignStmt" (.getName (.getClass unit))) 
     (if (not (= "soot.jimple.internal.JimpleLocal" (.getName (.getClass (.getRightOp unit)))))
       (doseq [x list] (doseq [y (.getUseBoxes (.getRightOp unit))] 
                         (if (=  (.toString (.getValue y)) x) (.add collec "access"))) 
                           (if (= (.toString (.getLeftOp  unit)) x) (.add collec "modify")))
       (doseq [r list] (if (= (.getName (.getRightOp unit)) r) (.add collec "access"))))
     (if (= "soot.jimple.internal.JInvokeStmt" (.getName (.getClass unit))) 
       (doseq [t list] (doseq [z (.getUseBoxes (.getInvokeExpr unit))] (if (= (.toString (.getValue z)) t) (.add collec "access"))))
       (if (= "soot.jimple.internal.JIfStmt" (.getName (.getClass unit))) 
         (doseq [m list] (if (or 
                               (= (.toString (.getOp1 (.getValue (.getConditionBox unit)))) m) 
                               (= (.toString (.getOp2 (.getValue (.getConditionBox unit)))) m)) (.add collec "access")))))))
 
 (defn- getRelatedLocals [param locals list]
   (doseq [x locals] (doseq [y (clojure.string/split param #",")] (if (and 
                                                                        (= "soot.jimple.internal.JIdentityStmt" (.getName (.getClass x))) 
                                                                        (= "soot.jimple.ParameterRef" (.getName (.getClass (.getRightOp x)))) 
                                                                        (= y (.toString (.getIndex (.getRightOp x))))) 
                                                                    (.add list (.getName (.getLeftOp x)))))))
 (defn- localArgs [?param ?locals ?list]
   (l/fresh [?res ?y]
     (equals ?res (java.util.ArrayList. []))
     (equals ?y (getRelatedLocals ?param ?locals ?res))
     (equals ?list ?res)))
 
 (defn- writeparameters [args]
   (str (if (and (false? (.isEllipsis (first args))) (false? (.isStar (first args)))) (.getFormalIndex (first args))) (if (not (empty? (rest args))) (str ","(writeparameters (rest args))))))

 (defn- collectArguments [args]
   (str (writeparameters (.getTypePatterns (.getArguments (first  args)))) (if (not (nil? (first (rest args)))) (str ","(collectArguments (rest args))))))
 
 (defn- addargs [lst selectedPointcut]
   (.add lst selectedPointcut)) 
  
 (defn- addlist [lst selectedPointcut]
   (.add lst selectedPointcut)) 
 
 (defn- getSignature [pointcut] 
   (.getSignature pointcut))
  
 (defn- getLeft [pointcut] 
   (.getLeft pointcut))
  
 (defn- getRight [pointcut] 
   (.getRight pointcut))
 
 (defn- getPKind [pointcut] 
   (.getPointcutKind pointcut))
 
 (defn- getKind [pointcut] 
   (.getKind pointcut))  
 
 (defn- getInterfaces-soot [?mn]
    (l/fresh [?m]
             (jsoot/soot :class ?m) 
             (equals ?mn (.getName ?m)) 
             (succeeds (.isInterface ?m))))
  
  (defn- getInterface-soot [?name] 
    (first (ekeko [?m]                   
                       (getInterfaces-soot ?m)
                       (equals true (= ?name ?m)))))
  
  (defn- getInterface [?name]
    (first (ekeko [?i] 
         (w/interface ?i)
         (l/fresh [?nameI]
            (equals ?nameI (.getName ?i))
            (equals true 
                (or  (= ?name ?nameI)
                     (= ?name "java.sql.Connection")
                     (= ?name "java.sql.PreparedStatement")
                     (= ?name "java.sql.ResultSet")
                     (= ?name "java.util.Collection")
                     (= ?name "java.util.Iterator")
                     (= ?name "javax.jms.QueueConnection")
                     (= ?name "javax.jms.TopicConnection")
                     (= ?name "javax.servlet.ServletContext")
                     (= ?name "javax.servlet.http.HttpServletRequest")
                     (= ?name "javax.servlet.http.HttpServletResponse")
                     (= ?name "javax.transaction.UserTransaction")
                     (= ?name "org.w3c.dom.Document")
                     (= ?name "org.w3c.dom.Element")
                     (= ?name "org.aspectj.lang.JoinPoint")
                     (= ?name "org.aspectj.lang.JoinPoint$StaticPart")
                     (= ?name "java.lang.annotation")
                     (= ?name "java.lang.Runnable")
                     (= ?name "java.util.Set")
                     (= ?name "java.sql.Statement")
                     (= ?name "java.util.Map")
                     (= ?name "javax.sql.DataSource")
                     (= ?name "java.sql.PreparedStatement")
                     (= ?name "javax.jms.TopicConnectionFactory")
                     (= ?name "javax.xml.transform.Source")
                     (= ?name "java.lang.annotation.Annotation")
                     (= ?name "org.xml.sax.XMLReader")
                     (= ?name "javax.jms.QueueConnectionFactory")
                     (= ?name "java.rmi.Remote")))))))

  (defn- getEnum [?name]
    (first (ekeko [?i] (w/enum ?i) (equals true (= ?name (.getClassName ?i))))))
 
  (defn- 
    soot|unit-getDeclarationClassname  
    [?method ?decName]  
    (equals ?decName (str "Aspect {"(.getShortName (.getDeclaringClass ?method)) "}")))

  (defn- 
    soot|unit-getInvokeExprBoxMethod
    [?method]
    (.getMethod (.getValue (.getInvokeExprBox ?method))))

  (defn- 
    soot|unit-getInvokeExprMethod
    [?method]
    (.getMethod (.getInvokeExpr ?method)))

  (defn- 
    lastIndexOfText 
    [from to]  
    (> (.lastIndexOf from to) -1))

  (defn-  
    IndexOfText  
    [from to](> (.indexOf from to) -1))