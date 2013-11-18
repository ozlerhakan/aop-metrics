(ns ekeko-ajfx.AOPMetrics
  (:refer-clojure :exclude [== type declare class])
  (:require [clojure.core.logic :as l]
            [clojure.core :as cr]
            [clojure.java.io :as io]
            [damp.ekeko.aspectj  
             [weaverworld :as w]  
             [soot :as ajsoot] 
             [ajdtastnode :as ajst] 
             [ajdt :as ajdt]]
            [damp.ekeko.soot     
             [soot :as jsoot]] 
            [damp.ekeko.jdt      
             [reification :as reif] 
             [javaprojectmodel :as jpm] 
             [basic :as bsc] 
             [astnode :as astn]])
  (:use [inspector-jay.core]
        [clojure.repl]
        [damp.ekeko logic]
        [damp.ekeko ekekomodel]
        [damp.ekeko gui]
        [damp.ekeko]
        [clojure.inspector :exclude [inspect]])
  (:import [soot.jimple IdentityStmt]
    [soot.jimple.internal JimpleLocal]
    [soot.jimple ThisRef ParameterRef]
    [org.aspectj.lang Signature]
    [java.lang Integer]
    [org.eclipse.jdt.core IJavaElement ITypeHierarchy IType IPackageFragment IClassFile ICompilationUnit
     IJavaProject WorkingCopyOwner IMethod]
    [org.eclipse.jdt.core.dom Expression IVariableBinding ASTParser AST IBinding Type TypeDeclaration 
     QualifiedName SimpleName ITypeBinding MethodDeclaration
     MethodInvocation ClassInstanceCreation SuperConstructorInvocation SuperMethodInvocation
     SuperFieldAccess FieldAccess ConstructorInvocation ASTNode ASTNode$NodeList CompilationUnit]
    [org.aspectj.weaver.patterns Pointcut AndPointcut]))

 (comment )
   
 ;############################### METRIC LOC ###############################
 ;the number of lines code per aspect except blank lines (comments & javadocs are still counted)
 ;(inspect (ekeko [?asp ?size]
 ;              (l/fresh [?Filelocation ?StringFileName]  
 ;                       (w/aspect ?asp)
 ;                       (equals ?Filelocation (.getSourceLocation ?asp))              
 ;                       (equals ?StringFileName (.getSourceFile ?Filelocation))
 ;                       (equals ?size (count (filter #(re-find #"\S" %) (line-seq (io/reader ?StringFileName))))))))

 ;############################### METRIC LOC ###############################
 ;count the number of lines of code per class except blank lines (comments & javadocs are still counted

 ;(defn pick-up-all-compilationUnits [?path ?classes]
 ;           (l/fresh [?project]
 ;                 (bsc/ast-project ?classes ?project)
 ;                 (equals  15 (.getNodeType ?classes));pick all compilationUnit (class)
 ;                  (equals ?path (.getPath (.getLocationURI (.getResource (.getJavaElement ?classes)))))))

 ;(defn class-LOC-v2 [?size ?classes]
 ;   (l/fresh [?class-path]
 ;           (pick-up-all-compilationUnits ?class-path ?classes)
 ;           (equals ?size (count (filter #(re-find #"\S" %) (line-seq (io/reader ?class-path)))))))
 ;(inspect (ekeko [?classes ?size] (class-LOC-v2 ?size ?classes )))
 ;############################### METRIC LOC ###############################
 ;count the number of lines of java code in a given project
 (defn class-loc [filePath]
  (reduce
    +
    (for [file (file-seq  filePath) :when (.endsWith (.toString file )".java")]
      (with-open [rdr (io/reader  file)] (count  (filter #(re-find #"\S" %) (line-seq rdr)))))))

;(class-loc (io/file "C:/Users/HAKAN/runtime-New_configuration-clojure/Clojure-1/src/"))
 ;(class-loc (io/file"C:/Users/HAKAN/Desktop/Thesis/ws/AJHotDraw/src/aspects"));36225
 ;(class-loc (io/file"C:/Users/HAKAN/workspace/PetstoreAspectJ/blueprints/petstore1.4/src"))
 (class-loc (io/file"C:/Users/HAKAN/runtime-New_configuration-clojure/Contract4J5/contract4j5/src"))
 ;############################### METRIC LOC ###############################
 ;count the number of lines of aspect code in a given project
 (defn aspect-loc [filePath]
  (reduce
    +
    (for [file (file-seq  filePath) :when (.endsWith (.toString file )".aj")]
      (with-open [rdr (io/reader file)] (count  (filter #(re-find #"\S" %) (line-seq rdr)))))))

;(aspect-loc (io/file "C:/Users/HAKAN/runtime-New_configuration-clojure/Clojure-1/src/"))
 ;(aspect-loc (io/file"C:/Users/HAKAN/Desktop/Thesis/ws/AJHotDraw/src/aspects"));2111
 ;(aspect-loc (io/file "C:/Users/HAKAN/workspace/PetstoreAspectJ/blueprints/petstore1.4/src"))
 (class-loc (io/file "C:/Users/HAKAN/runtime-New_configuration-clojure/Contract4J5/contract4j5/src"))
 ;############################### METRIC VS ###############################
 ;the number of JAVA classes (.java) in a selected project
 (defn class-count [filePath]
  (reduce
    +
    (for [file (file-seq filePath) :when (.endsWith (.toString file ) ".java")] 1) ))
 (class-count (io/file "C:/Users/HAKAN/runtime-New_configuration-clojure/Contract4J5/contract4j5/src"));

 ;Number of Classes in the project except enums, interfaces,and their sub-classes!!
 (defn NOClasses [?classes]
            (l/fresh []
                  (w/class ?classes)
                  (equals false (or                                   
                                  (.isEnum ?classes)
                                  (IndexOfText (.getName ?classes) "$")
                                  (IndexOfText (.getName ?classes) "MainTST")
                                  (IndexOfText (.getName ?classes) "Maincontract");our initial main to activate soot analysis, so I ignore it
                                  (IndexOfText (.getName ?classes) "lang.Object")))))
 
 (inspect (ekeko [?c] (NOClasses ?c)))

 ;the number of aspects in a selected project -include sub-aspects
 (inspect (ekeko [?aspects ?source]  
                 (w/aspect ?aspects) 
                 (equals ?source (.getSourceLocation ?aspects) )));just to be sure!

 ;the number of aspectj (.aj) files in a project!  Just an additional query, it is not relating with the main query!  
 (defn aspect-count [filePath]
  (reduce
    +
    (for [file (file-seq filePath) :when (.endsWith (.toString file )".aj")] 1) ))
 (aspect-count (io/file "C:/Users/HAKAN/workspace/PetstoreAspectJ/blueprints/petstore1.4/src"))

 ;############################### METRIC NOAttributes-fields ###############################
 (inspect  (ekeko [?fields] (w/field ?fields)));get the entire fields from the weaver
 
 ;only works for aspects to count the number of fields!
 (inspect (ekeko [?fields] (ajdt/field ?fields)));Relation of fields declared ONLY within aspects
 ;(count (ekeko [?fields] (ajdt/field ?fields )));
 (inspect (ekeko [?aspect ?f] (ajdt/aspect-field ?aspect ?f)));same as ajdt/field; for aspect!

 ;get a specific class and its fields in terms of a package name
 (inspect (ekeko [?t ?f] 
                 (w/type-fields ?t ?f)
                 (succeeds (IndexOfText (.toString ?t) "InvariantTypeConditions"))))
 
;count-fields-in both CLASSES and ASPECTS except their sub-classes' fields!!
(defn count-fields-in-modules [?c ?f]
         (l/fresh []
                     (w/type-field ?c ?f)
                     (equals false (.isEnum ?c))
                     (equals false (and (.isClass ?c) (IndexOfText  (.getName ?c) "$")))
                     (succeeds (empty? (filter #(re-matches #"\S+\$[1-9]+" %)  [(.getName ?c)])))
                     (equals false (or 
                                     (.startsWith (.getName ?f) "ajc")
                                     (.startsWith (.getName ?f) "this")
                                     (IndexOfText (.getName ?f) "$")))))
                     
 (inspect (sort-by first (ekeko [?cn ?f] (l/fresh [?c] (count-fields-in-modules ?c ?f ) (equals ?cn (.getName ?c))))))
 (inspect (count (ekeko [?c ?f] (count-fields-in-modules ?c ?f)))) 
 
 (comment 
 ;#### jsoot/soot-class-field function still does not get some fields in a project!!
 (inspect  (ekeko [?c ?f]
                (jsoot/soot-class-field ?c ?f)
                (equals false (or  
                                (IndexOfText  (.getName ?c) "aspectj")    
								                (IndexOfText  (.getName ?c) "apache")
								                (.startsWith  (.getPackageName ?c) "groovy")
								                (IndexOfText  (.getName ?c) "objectweb")
                                (IndexOfText  (.getName ?c) "codehaus")
								                (IndexOfText  (.getPackageName ?c) "antlr")
								                (IndexOfText  (.getPackageName ?c) "junit")
								                (IndexOfText  (.getName ?c) "$")
                                (= "Enum" (.getShortName (.getSuperclass ?c)))))
               (succeeds (empty? (filter #(re-matches #"\S+\$[1-9]+" %)  [(.getName ?c)])))
               (equals false (or 
                               (.startsWith (.getName ?f) "ajc")
                               ;(.startsWith (.getName ?f) "$")
                               (IndexOfText (.getName ?f) "$")
                               (.startsWith (.getName ?f) "this")))
               (succeeds (IndexOfText (.toString (.getName ?c)) "org.contract4j5.interpreter.bsf.jexl.JexlBSFEngine" ))));
 )
 
 ;############################### METRIC NOO ###############################
 ;METHODS: both all classes and aspects
 ;(inspect (ekeko [?methods] (w/method ?methods)))

 ;NUMBER OF METHODS: only responsible for classes!  - construct methods including 
 (defn classes-methods [?typesn ?methods]
                (l/fresh [?types]
                         (w/type-method ?types ?methods)
                         (equals false (or
                                         (= "java.lang.Object" (.getName ?types))
                                         (.isAspect ?types)
                                         (.isEnum ?types)
                                         (.isInterface ?types)))
                         (equals ?typesn (.getName ?types))
                         (succeeds (empty? (filter #(re-matches #"\S+\$\S+" %) [(.getName ?types)])))
                         (equals false (= "STATIC_INITIALIZATION" (.toString (.getKind ?methods))))
                         (succeeds (empty? (filter #(re-matches #"\S+\$[1-9]+" %) [(.getName ?methods)])))))
                         ;(succeeds (empty? (filter #(re-matches #"\S+\$[1-9]+" %) [(.getName (.getDeclaringType ?methods))])))
                         ;(succeeds (IndexOfText (.toString (.getName ?types)) "org.contract4j5.reporter.Severity" ))))
 ;1 Classs
 (inspect (sort-by first (ekeko [?types ?m] (classes-methods ?types ?m))))
 (count ( ekeko [?types ?m] (classes-methods ?types ?m)))

 ;exclude construct methods of classes!
 (defn classes-methods-with-noconstructs [?t ?m]
             (l/fresh []
                  (classes-methods ?t ?m)
                  (equals false (= 3 (.getKey (.getKind ?m))))))
 ;1 Class
 (inspect(sort-by first (ekeko [?t ?m] (classes-methods-with-noconstructs ?t ?m))))
 (count (ekeko [?t ?m] (classes-methods-with-noconstructs ?t ?m)))

 ;2 Aspect : find basic method declarations in aspect files
 (inspect (sort-by first (ekeko [?file ?methodName] 
                                (l/fresh [?method-aspect]
                                         (ajdt/method ?method-aspect)
                                         (equals ?methodName (.getElementName ?method-aspect))
                                         (equals ?file (.getName (.getResource ?method-aspect)))))))
 ;2 Aspect : find basic method declarations in aspect files
 (defn aspects-methods [?types ?methods]
                (l/fresh [?tname ?methodName]
                        (w/type-method ?types ?methods)
                        (equals ?tname (.getClassName ?types))
                        (equals ?methodName (.getName ?methods))
                        (succeeds (.isAspect ?types))
                        (equals false (= 8 (.getModifiers ?methods)))
                        (equals false (.startsWith (.getName ?methods) "ajc"))
                        (equals false (or (= "hasAspect" (.getName ?methods)) 
                                          (= "aspectOf" (.getName ?methods)) 
                                          (= "<init>" (.getName ?methods))))))

 (inspect (sort-by first  (ekeko [?tn ?m] (l/fresh [?t] (aspects-methods ?t ?m) (equals ?tn (.getClassName ?t))))))
 ; <= without intertyped method declarations
 ;-------------------------------------------------------------
 ;    intertyped method declarations =>

 ;METHODS: only intertyped method declarations in aspect
 ;1 Aspect
 (defn aspects-intertyped-methods [?decAspect ?methods]
                (l/fresh [?types]
                          (w/type-method ?types ?methods)
                          (succeeds   (.isAspect ?types))
                          (equals ?decAspect (.getName (.getDeclaringType ?methods)))
                          (succeeds   (.startsWith (.getName ?methods) "ajc$interMethod$"))));$AFTER,$BEFORE, so on...

 (inspect (sort-by first (ekeko [?decAspect ?methods] (aspects-intertyped-methods  ?decAspect ?methods))))
 (inspect (count (ekeko [?get ?decAspect] (aspects-intertyped-methods ?get ?decAspect))))
  
 ;get the all intertype method declaration implemented in a project
 ;--- the difference from the above query is that this query also reaches abstract intertype methods
 (inspect (sort-by first  (ekeko [?aspect ?interName ?i] 
                                 (l/fresh [] 
                                          (w/intertype|method ?i)
                                          (equals ?aspect (.getName (.getAspectType ?i)))
                                          (equals ?interName (.getName (.getSignature ?i)))))))
  
 ;COUNT: 1 class + 2 aspect + 1 aspect :RESULT combines with 

 ;(inspect (count (ekeko [?method-aspect] (ajdt/method ?method-aspect))))
 ;-------------------------------------------------------------------------------------------------
 ;NUMBER OF ADVICES: only for aspects;
(defn NOFA-in-aspects [?aspect ?adv]  
              (l/fresh []
                   (w/advice ?adv)
                   (equals ?aspect (.toString (.getConcreteAspect ?adv)))
                   (equals false (IndexOfText (.getName (.getClass ?adv)) "Checker"))
                   (equals false (or
                                   (or (.isCflow (.getKind ?adv)) (.isPerEntry (.getKind ?adv)))
                                   (= "softener" (.getName (.getKind ?adv)))))));exclude perThis, perTarget , perCflow, Cflow, CflowBelow, softener, and Checker(warning)!!

(inspect (sort-by first (ekeko [?a ?adv] (NOFA-in-aspects ?a ?adv))))
(count (ekeko [?a ?adv] (NOFA-in-aspects ?a ?adv)))

;(inspect (ekeko [?softener] (w/declare|soft ?softener)))
;(inspect (ekeko [?dec] (w/declare|warning  ?dec)))
 
 ;GET THE ADVICES that have EMPTY POINTCUTS
 (inspect (ekeko [?as ?ad]
                 (l/fresh [?point ?po]
                 (w/aspect-advice ?as ?ad)
                 (equals ?po (.getPointcut ?ad))
                 (equals  true (empty? (.toString  ?po))))))
 
 ;############################### Advice-Method dependence (AM) :the number of method calls per advice body ###############################

 (inspect (ekeko [?advices] (w/advice ?advices)))
 ;only get the declaration of advices
 (inspect  (ekeko [?advices] (ajdt/advice ?advices)));Gets the same result like the NOFA-in-aspects function

;Additional query: get the entire advice soot methods of the selected advice package
 (defn get-advices-soot|methods [?soot|advicemethod ?units]
               (l/fresh [?adv ?aspect]
                         (NOFA-in-aspects ?aspect ?adv);I am using this function or I can also use ajdt/advice function to get the proper declarded advice in a project and to avoid the errors!
                         (ajsoot/advice-soot|method ?adv ?soot|advicemethod)
                         (succeeds (.hasActiveBody ?soot|advicemethod))
                         (equals ?units (.getUnits (.getActiveBody ?soot|advicemethod)))))
                         ;(equals true (=  "org.contract4j5.debug.ReportThrows" (.getName (.getDeclaringClass ?soot|advicemethod))))))

 (inspect (ekeko [?soot|advicemethod ?units] (get-advices-soot|methods ?soot|advicemethod ?units)))
 
 (defn get-signature-advice|method [?signature]
               (l/fresh [?adv ?aspect ?soot]
                         (NOFA-in-aspects ?aspect ?adv)
                         (ajsoot/advice-soot|method ?adv ?signature)))
                         ;(ajsoot/advice-soot|method2 ?adv ?signature)
                         ;(jsoot/soot-method-signature ?soot ?signature)))
  
 (inspect (ekeko [?s] (get-signature-advice|method ?s)))
 
 ;find all SootMethods
(inspect (ekeko [?m]
  (jsoot/soot :method ?m)))

 ;get the all transforming classes with a given part of a class name
 (inspect (ekeko [?model ?scne ?class]
             (l/fresh [?classes]
                      (jsoot/soot-model-scene ?model ?scne)
                      (equals ?classes (.getClasses ?scne))
                      (contains ?classes ?class)
                      (succeeds (IndexOfText (.getName ?class) "org.contract4j5.debug")))))

;################################## WORKING!! (I hope so) ####################################
 (defn NOMethodCalls-perAdvice [?aspectName ?calledmethods ?soot|methodName]; ?method : class soot.SootMethod
          (l/fresh [?aspect ?advices ?units ?sootMDeclass ?soot|method]
            (NOFA-in-aspects ?aspect ?advices)
	          (ajsoot/advice-soot|method ?advices ?soot|method)
            (succeeds (.hasActiveBody ?soot|method))
            (soot|unit-getDeclarationClassname ?soot|method ?aspectName)
            (equals ?units (.getUnits (.getActiveBody ?soot|method)))
            (equals ?soot|methodName (.getName ?soot|method))
            (contains ?units ?calledmethods)
            ;(jsoot/soot|method-soot|unit ?soot|method ?calledmethods)
            (succeeds (.containsInvokeExpr ?calledmethods))
	          (succeeds (or (= "soot.jimple.internal.JAssignStmt" (.getName (.getClass ?calledmethods)))  
	                        (= "soot.jimple.internal.JInvokeStmt" (.getName (.getClass ?calledmethods)))))	          
	          (equals ?sootMDeclass (.getShortName (.getDeclaringClass (.getMethod (.getInvokeExpr ?calledmethods)))))
	          (equals false (= "StringBuilder" ?sootMDeclass))
	          (equals false (or  (IndexOfText  ?sootMDeclass "aspectj")
	                             (IndexOfText  ?sootMDeclass "apache")
                               (IndexOfText  ?sootMDeclass "CFlowCounter")))
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
		                     (lastIndexOfText (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "$advice")
                         (=     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "valueOf")
                         (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$")
		                    ;(.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$around$")
                         (.startsWith     (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "access$"))))
	          (equals false
                (or
                  (= "aspectOf" (.getName (.getMethod (.getValue (.getInvokeExprBox ?calledmethods)))))
                  (= "makeJP" (.getName (.getMethod (.getValue (.getInvokeExprBox ?calledmethods)))))))))
	          ;(equals true (= "spacewar.Display$DisplayAspect" (.getName (.getDeclaringClass ?soot|method))))))

 (inspect (sort-by first (ekeko [?aspect ?soot|method ?calledmethods] (NOMethodCalls-perAdvice ?aspect ?calledmethods ?soot|method))))
 ;COUNT
 ;(count (ekeko [?aspect ?soot|method ?calledmethods] (NOMethodCalls-perAdvice ?aspect ?calledmethods ?soot|method)))

 ;soot|value|invocation-soot|method
 ;soot.jimple.internal.JAssignStmt
 ;soot.jimple.internal.JInvokeStmt

 ;(inspect (ekeko [?method ?call]
 ;  (jsoot/soot|method-soot|unit ?method ?call))) ;?call : class soot.jimple.internal.JInvokeStmt,JInterfaceInvoke,JSpecialInvoke,JStaticInvoke ,JVirtualInvoke

 ;(inspect (ekeko [?t ?m]  (w/type-method ?t ?m)))
 ;(println "type-method")

 ;(inspect (ekeko [?caller ?callee ?call ?receiver]  ( method-methodCalls ?caller ?callee ?call ?receiver)))
 ;(inspect (ekeko [?advice ?unit] (advice-soot|unit ?advice ?unit)))
 
 ;############################### Attribute-Class dependence Measure (AtC) ###############################
 ;Definition : if a class is the type of an field of an aspect - - count the number of types that belong to fields in aspects
 ;Filtering primitive types and interfaces that could be the type of a field!
  (defn getField-AtC [?aspectName ?fieldName ?fieldType ?tcname] 
         (l/fresh [?field ?aspect ?isSameInterface ?signature]
                 (w/type-field ?aspect ?field)
                 (succeeds (.isAspect ?aspect))
                 (equals ?aspectName (.getName ?aspect))
                 (equals ?fieldType  (.getType ?field))
                 ;(equals ?signature  (.getName (.getType ?field)))
                 (equals false       (.isPrimitiveType (.getType ?field))); I ignore primitive types such as boolean, int , void ,double, and so on.
                 (equals false       (or (.startsWith (.getName ?field) "ajc") (.startsWith (.getName ?field) "this")))
                 (equals ?tcname     (.getName ?fieldType))
                 (equals ?isSameInterface (getInterface ?tcname))
                 (equals true        (nil? ?isSameInterface));check whether the type is interface or not!!
                 (equals ?fieldName  (str "<Field name: " (.getName ?field) ">"))))
 
 (inspect (sort-by first (ekeko [?t ?f ?typef ?sig] (getField-AtC ?t ?f ?typef ?sig))))
 ;############################### Advice-Class  dependence (AC) ###############################
 ; if a class is  the type of a parameter of a piece of advice of an aspect 
 (defn getAC-p1 [?aspect ?adviceKind ?typename] 
   (l/fresh [?typesofAdvice ?advice  ?isInterface ?parameter]
            (NOFA-in-aspects ?aspect ?advice)
            (equals   ?adviceKind (.getKind ?advice))
            (equals   ?typesofAdvice (.getParameterTypes (.getSignature ?advice)))
            (contains ?typesofAdvice ?parameter)
            (equals   ?typename  (.getName ?parameter))
            (equals   ?isInterface (getInterface ?typename));control whether a selected type is interface that was implemented in a given AspectJ app 
            (succeeds (nil? ?isInterface))
            (equals false (.isPrimitiveType ?parameter))))
            ;(equals false (IndexOfText  ?typename "AroundClosure"))))            
 
 (inspect  (sort-by first (ekeko [?as ?a ?r] (getAC-p1 ?as ?a ?r)))) 
 
 ; the return type of the piece of advice - around - ; "after returning" is being checked in the above function called -getAC-p1-
 (defn getAC-p2 [?aspect ?adviceKind ?returntypename] 
   (l/fresh [?advice ?isInterface ?returntype]
            (NOFA-in-aspects ?aspect ?advice)
            (equals ?adviceKind (.getKind ?advice))
            (succeeds (= 5 (.getKey (.getKind ?advice))))
            (equals ?returntype (.getReturnType (.getSignature ?advice)))
            (equals ?returntypename (.getName ?returntype))
            (equals false (.isPrimitiveType ?returntype))
            (equals ?isInterface (getInterface ?returntypename))
            (succeeds  (nil? ?isInterface))))
  
  (inspect  (sort-by first (ekeko [?r ?as ?a] (getAC-p2 ?as ?a ?r))))
  
  ;combined the two queries in one inspect
  (inspect (sort-by first  (clojure.set/union
                            (ekeko [?vari ?as ?ad ] (getAC-p1 ?as ?ad ?vari))
                            (ekeko [?vari ?as ?ad ] (getAC-p2 ?as ?ad ?vari)))))
 ;############################### Intertype method-Class dependence (IC) ###############################
 ;if classes are the type of  parameters or return type of intertype method declarations in aspects of a given AspectJ App
 
 ;find all return types of intertype method declarations
 (defn measureIC-returnType [?aspect ?interName ?type ?returnname] 
         (l/fresh [?isInterface ?i ?return] 
                  (w/intertype|method ?i)
                  (equals ?aspect (.getName (.getAspectType ?i)))
                  (equals ?return (.getReturnType (.getSignature ?i)))
                  (equals false (.isPrimitiveType ?return));except primitive types
                  (equals ?returnname  (.getName ?return))
                  (equals ?isInterface (getInterface ?returnname));except interfaces
                  (succeeds  (nil? ?isInterface))
                  (equals ?type (str "RETURN"))
                  (equals ?interName (str (.getClassName (.getDeclaringType (.getSignature (.getMunger ?i))))"."(.getName (.getSignature ?i))))))
 
 (inspect (sort-by first  (ekeko [?aspect ?interName ?type ?return] (measureIC-returnType ?aspect ?interName ?type ?return))))
 ;find all parameter types of intertype method declarations
 (defn measureIC-parameters [?aspect ?interName ?param ?variName] 
	       (l/fresh [?v ?isInterface ?i ?vari] 
	                (w/intertype|method ?i)
	                (equals ?aspect (.getName (.getAspectType ?i)))
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
  
 ;############################### Method-Class dependence (MC) ###############################
 ;Definition: if classes are the type(s) of parameters or return type(s) of method declarations in aspects of a given AspectJ App
 (defn measureMC-param [?aspectn ?methodN ?paramName ?rtype] 
   (l/fresh [?aspect ?params ?method ?param ?isInterface]
     (aspects-methods ?aspect ?method)
     (equals ?aspectn (.getName ?aspect))
     (equals ?params (.getParameterTypes ?method))
     (contains ?params ?param)
     (equals false (.isPrimitiveType ?param))
     (equals ?paramName (.getName ?param))
     (equals ?isInterface (getInterface ?paramName));except interface classes
     (succeeds  (nil? ?isInterface))
     (equals ?methodN (.getName ?method))
     (equals ?rtype (str "PARAM"))))
  
 (inspect (sort-by first  (ekeko [?p ?A ?m ?r] (measureMC-param ?A ?m ?p ?r))))
  
 (defn measureMC-return [?aspectn ?methodN ?returnName ?rtype] 
   (l/fresh [?aspect ?return ?method ?isInterface]
     (aspects-methods ?aspect ?method)
     (equals ?aspectn (.getName ?aspect))
     (equals ?return (.getReturnType ?method))
     (equals false (.isPrimitiveType ?return))
     (equals ?returnName (.getName ?return))
     (equals ?isInterface (getInterface ?returnName));except interface classes
     (succeeds  (nil? ?isInterface))
     (equals ?methodN (.getName ?method))
     (equals ?rtype (str "RETURN"))))
  
  (inspect (sort-by first  (ekeko [?p ?A ?m ?r] (measureMC-return ?A ?m ?p ?r))))
   
  ;combined the two queries in one inspect
  (inspect (sort-by first (clojure.set/union
                            (ekeko [?vari ?A ?m ?r] (measureMC-param ?A ?m ?vari ?r))
                            (ekeko [?vari ?A ?m ?r] (measureMC-return ?A ?m ?vari ?r)))))
  
  ;############################### Pointcut-Class dependence (PC) ###############################
  ;if a class is the type of a parameter of a pointcut (poincutDefinition) in an aspect
  (defn measurePC [?typename ?pn ?aspect] 
    (l/fresh [?point ?types ?type ?isInterface]
             (w/pointcutdefinition ?point)
             (equals ?types (.getParameterTypes ?point))
             (contains ?types ?type)
             (equals false (.isPrimitiveType ?type))
             (equals ?typename (.getName ?type))
             (equals ?isInterface (getInterface ?typename))
             (succeeds  (nil? ?isInterface))
             (equals ?aspect (.getName (.getDeclaringType ?point)))
             (equals ?pn (str "<Pointcut Name :"(.getName ?point)">"))))
  
  (inspect (sort-by first (ekeko [?tn ?pn ?aspect] (measurePC ?tn ?pn ?aspect))))
  
  ;############################### Pointcut-Method dependence (PM) ###############################
  ;if a pointcut of an aspect contains at least one join point that is related to a method of a class
  (defn countPM [?className ?shadowStr ?adviceKind ?aspectName ?pointcut] 
                  (l/fresh [?aspect ?advice ?shadow]
                           (NOFA-in-aspects ?aspect ?advice)
                           (w/advice-shadow ?advice ?shadow);in order to reach the join point shadows, I used w/advice-shadow to pick them up along with advices' poincuts 
                           (equals false (.isCode ?shadow))
                           (succeeds (= "class" (.toString (.getKind (.getParent ?shadow)))))
                           (equals ?className (.getName (.getParent ?shadow)))
                           (equals ?adviceKind (.getKind ?advice))
                           (equals ?aspectName (.toString ?aspect))
                           (equals ?shadowStr (str "<Join Point Shadow : "(.toString ?shadow)">"))
                           (equals ?pointcut (.getPointcut ?advice))))
  
  (inspect (sort-by first (ekeko [?className ?shadow ?adviceKind ?aspect ?pointcut] (countPM ?className ?shadow ?adviceKind ?aspect ?pointcut))))
  ;############################### NOPointcuts ############################### ;aspect or class and its pointcut definitions
 (inspect (ekeko [?type ?pointdef] (w/type-pointcutdefinition ?type ?pointdef )))
 ;count aspects and its poincuts' definitions
 (inspect (ekeko [?aspects ?pointcuts] (w/aspect-pointcutdefinition ?aspects ?pointcuts )))

 ;get the all poincuts even the anoymous poincuts
 (inspect(ekeko [?point] (w/pointcut ?point)))

 ;just count the number of poincuts declared  properly in aspects 
 (inspect (ekeko [?point] (w/pointcutdefinition ?point)))

 ;pointcut and its primitive pointcuts; if there is no primitive pointcuts of a pointcut , the pointcut wont show
 (inspect (ekeko [?pointdef ?point] (w/pointcutdefinition-pointcut ?pointdef ?point)))

 (inspect (ekeko [?point] (ajdt/pointcut ?point)))

 ;advice & the definition of its pointcuts
 ;if it is not pointcut definion of the advice, the advice will not show off
 (inspect (ekeko [?advice ?pointdef] (w/advice-pointcutdefinition ?advice ?pointdef)))

 ;############################### SHADOWS ###############################

 (inspect (ekeko [?shadow ?type] (w/shadow-ancestor|type ?shadow ?type)))

 (inspect (ekeko [?shadow ?class] (w/shadow-ancestor|class ?shadow ?class))) 
 (inspect (ekeko [?shadow ?aspect] (w/shadow-ancestor|aspect ?shadow ?aspect)))
 

 ;##########################################################################
 
 (inspect (ekeko [?c ?aspect] (ajdt/compilationunit-aspect ?c ?aspect)))
 
  ;)
  
  (defn- getInterfaces-soot [?mn]
         (l/fresh [?m]
             (jsoot/soot :class ?m) 
             (equals ?mn (.getName ?m)) 
             (succeeds (.isInterface ?m))))
  
  ;(inspect (sort-by first (ekeko [?s] (getInterfaces-soot ?s))))
  
  (defn- getInterface-soot [?name] 
    (first (ekeko [?m]                   
                       (getInterfaces-soot ?m)
                       (equals true (= ?name ?m)))))
  
  ;(inspect (sort-by first (getInterface-soot "java.sql.ResultSet")))
  
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
                     (= ?name "java.rmi.Remote")))))))

  (defn- getEnum [?name]
        (first (ekeko [?i] (w/enum ?i) (equals true (= ?name (.getClassName ?i))))))
 
  (defn- 
   soot|unit-getDeclarationClassname  
   [?method ?decName]  
   (equals ?decName (.getName (.getDeclaringClass ?method))))

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
 
