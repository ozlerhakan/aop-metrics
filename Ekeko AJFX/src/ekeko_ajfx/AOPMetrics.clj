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
    [org.eclipse.jdt.core IJavaElement ITypeHierarchy IType IPackageFragment IClassFile ICompilationUnit
     IJavaProject WorkingCopyOwner IMethod]
    [org.eclipse.jdt.core.dom Expression IVariableBinding ASTParser AST IBinding Type TypeDeclaration 
     QualifiedName SimpleName ITypeBinding MethodDeclaration
     MethodInvocation ClassInstanceCreation SuperConstructorInvocation SuperMethodInvocation
     SuperFieldAccess FieldAccess ConstructorInvocation ASTNode ASTNode$NodeList CompilationUnit]
    [org.aspectj.weaver.patterns Pointcut AndPointcut]))

 (comment )
;##################################### METRIC LOC ###################################
 ;the number of lines code per aspect except blank lines (comments & javadocs are still counted)
 ;(inspect (ekeko [?asp ?size]
 ;              (l/fresh [?Filelocation ?StringFileName]  
 ;                       (w/aspect ?asp)
 ;                       (equals ?Filelocation (.getSourceLocation ?asp))              
 ;                       (equals ?StringFileName (.getSourceFile ?Filelocation))
 ;                       (equals ?size (count (filter #(re-find #"\S" %) (line-seq (io/reader ?StringFileName))))))))

 ;##################################### METRIC LOC ###################################
 ;count the number of lines of code per class except blank lines (comments & javadocs are still counted

 (defn pick-up-all-compilationUnits [?path ?classes]
            (l/fresh [?project]
                  (bsc/ast-project ?classes ?project)
                  (equals  15 (.getNodeType ?classes));pick all compilationUnit (class)
                   (equals ?path (.getPath (.getLocationURI (.getResource (.getJavaElement ?classes)))))))

 (defn class-LOC-v2 [?size ?classes]
    (l/fresh [?class-path]
            (pick-up-all-compilationUnits ?class-path ?classes)
            (equals ?size (count (filter #(re-find #"\S" %) (line-seq (io/reader ?class-path)))))))

;(inspect (ekeko [?classes ?size] (class-LOC-v2 ?size ?classes )))
 ;###################################### METRIC LOC ################################### PASS
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
 ;##################################### METRIC LOC ###################################
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
 ;#################################### METRIC VS ##################################### PASS
 ;the number of JAVA classes (.java) in a selected project
 (defn class-count [filePath]
  (reduce
    +
    (for [file (file-seq filePath) :when (.endsWith (.toString file ) ".java")] 1) ))
 (class-count (io/file "C:/Users/HAKAN/runtime-New_configuration-clojure/Contract4J5/contract4j5/src"));

 ;Number of Classes in the project except their sub-classes, enums, and interfaces!!
 (defn NOClasses [?classes]
            (l/fresh []
                  (w/class ?classes)
                  (equals false (or                                   
                                  (.isEnum ?classes)
                                  (IndexOfText (.getName ?classes) "$")
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
 (aspect-count (io/file "C:/Users/HAKAN/workspace/PetstoreAspectJ/blueprints/petstore1.4/src"));

 ;########################## METRIC NOAttributes-fields ############################# 
 (inspect  (ekeko [?fields] (w/field ?fields)));get the entire fields from the weaver
 
 ;only works for aspects to count the  number of fields!
 (inspect (ekeko [?fields] (ajdt/field ?fields)));Relation of fields declared ONLY within aspects
 ;(count (ekeko [?fields] (ajdt/field ?fields )));
 (inspect (ekeko [?aspect ?f] (ajdt/aspect-field ?aspect ?f)));same as ajdt/field; for aspect!

 ;get a specific class and its fields in terms of a package name
 (inspect (ekeko [?t ?f] 
                 (w/type-fields ?t ?f)
                 (succeeds (IndexOfText (.toString ?t) "org.contract4j5.interpreter.groovy.GroovyExpressionInterpreter"))))
 
;count-fields-in both CLASSES and ASPECTS except their subclasses' or sub-aspects' fields!!
(defn count-fields-in [?c ?f]
         (l/fresh []
                     (w/type-field ?c ?f)
                     ;(succeeds (or (.isClass ?c) (.isAspect ?c)))
                     (equals false (.isEnum ?c))
                     (equals false (IndexOfText  (.getName ?c) "$"))
                     (succeeds (empty? (filter #(re-matches #"\S+\$[1-9]+" %)  [(.getName ?c)])))
                     (equals false (or 
                                     (.startsWith (.getName ?f) "ajc")
                                     ;(.startsWith (.getName ?f) "$")
                                     (.startsWith (.getName ?f) "this")
                                     (IndexOfText (.getName ?f) "$")))))
                     
 (inspect (ekeko [?c ?f] (count-fields-in ?c ?f)))
 (inspect (count (ekeko [?c ?f] (count-fields-in ?c ?f))))
 
 (comment 
 ;#### jsoot/soot-class-field function still does not get some fields in a project!!!!
 (inspect  (ekeko [?c ?f]
                (jsoot/soot-class-field ?c ?f)
                (equals false (or  
                                (IndexOfText  (.getName ?c) "aspectj")   
								                (IndexOfText  (.getName ?c) "apache")
								                (.startsWith  (.getPackageName ?c) "groovy")
								                (IndexOfText  (.getName ?c) "objectweb")
                                (IndexOfText  (.getName ?c) "codehaus")
								                (IndexOfText  (.getName ?c) "antlr")
								                (IndexOfText  (.getName ?c) "junit")
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
 
 ;#################################### METRIC NOO ###################################
 ;METHODS: both all classes and aspects
 ;(inspect (ekeko [?methods] (w/method ?methods)))

 ;NUMBER OF METHODS: only responsible for classes!  
 (defn classes-methods [?types ?methods]
                (l/fresh []
                         (w/type-method ?types ?methods)
                         (equals false (or
                                         (= "java.lang.Object" (.getName ?types))
                                         (.isAspect ?types)
                                         (.isEnum ?types)
                                         (.isInterface ?types)))
                         (equals false (= "STATIC_INITIALIZATION" (.toString (.getKind ?methods))))
                         (equals false (.isInterface ?methods))
                         (succeeds (empty? (filter #(re-matches #"\S+\$[1-9]+" %) [(.getName (.getDeclaringType ?methods))])))
                         (succeeds (empty? (filter #(re-matches #"\S+\$\S+" %) [(.getName ?types)])))))
                         ;(succeeds (IndexOfText (.toString (.getName ?types)) "org.contract4j5.reporter.Severity" ))))
;1 Classs
(inspect (ekeko [?types ?m] (classes-methods ?types ?m)))
(inspect (count ( ekeko [get] (classes-methods get))))

 ;2 Aspect : find basic method declarations in aspect files
 (inspect  (ekeko [?method-aspect] (ajdt/method ?method-aspect)))

 ; <= without intertyped method declarations
 ;----------------------------------------------
 ;    intertyped method declarations =>

 ;METHODS: only intertyped method declarations in aspect
 ;1 Aspect
 (defn aspects-intertyped-methods [?methods]
                (l/fresh [?types]
                         (w/type-method ?types ?methods)
                          (equals false  (or 
                                            (= "STATIC_INITIALIZATION" (.toString (.getKind ?methods))) 
                                            (= "CONSTRUCTOR" (.toString (.getKind ?methods)))))
                          (succeeds   (.isAspect ?types))
                          (equals false  (.isAjSynthetic ?methods)) 
                          (equals false  (or 
                                           (= "hasAspect" (.getName ?methods)) 
                                           (= "<init>" (.getName ?methods))))
                          (equals false  (.isInterface ?methods))
                          (succeeds   (.startsWith (.getName ?methods) "ajc$interMethod$"))));$AFTER,$BEFORE, so on...

 (inspect (ekeko [?get] (aspects-intertyped-methods ?get)))
 (inspect (count (ekeko [?get] (aspects-intertyped-methods ?get))))
 
 (inspect (count (ekeko [?intertype] (w/intertype|method ?intertype)))) 
 (inspect  (ekeko [?method-aspect] (ajdt/method ?method-aspect)))
  
 ;COUNT: 1 class + 2 aspect + 1 aspect :RESULT combines with 

 ;(inspect (count (ekeko [?method-aspect] (ajdt/method ?method-aspect))))
 ;----------------------------------------------
 ;NUMBER OF ADVICES: only for aspects
(defn NOFA-in-aspects [?aspect ?adv]  
              (l/fresh []
                   (w/advice ?adv)
                   (equals ?aspect (.getConcreteAspect ?adv))))
(inspect (ekeko [?a ?adv] (NOFA-in-aspects ?a ?adv)));include Cflow, softener!!
(inspect (ekeko [?softener] (w/declare|soft ?softener)));in order to delete softener parts from the NOFA-in-aspects , use this query!

 ;(count (ekeko [?adv] (NOFA-in-aspects ?adv)))
 ;############# Advice-Method dependence (AM) :the number of method calls per advice body #######

 (inspect (ekeko [?advices] (w/advice ?advices)))
(inspect  (ekeko [?advices] (ajdt/advice ?advices)))

;Additional query: get the entire advice soot methods of the selected advice
 (defn get-advices-soot|methods [?soot|advicemethod ?units]
               (l/fresh [?adv]
                         (ajsoot/advice-soot|method ?adv ?soot|advicemethod)
                          (equals true (=  "org.contract4j5.aspects.InvariantCtorConditions" (.getName (.getDeclaringClass ?soot|advicemethod))))))

 (inspect (ekeko [?soot|advicemethod ?units] (get-advices-soot|methods ?soot|advicemethod ?units)))

;################################## WORKING!! (I hope so) ####################################
 (defn NOMethodCalls-perAdvice [?advices ?calledmethods ?soot|method]; ?method : class soot.SootMethod
          (l/fresh [?sootMDeclass ?ExprBox ?Expr]
	          (ajsoot/advice-soot|method ?advices ?soot|method)
	          (jsoot/soot|method-soot|unit ?soot|method ?calledmethods)
	          (succeeds (or (= "soot.jimple.internal.JAssignStmt" (.getName (.getClass ?calledmethods)))  
	                        (= "soot.jimple.internal.JInvokeStmt" (.getName (.getClass ?calledmethods)))))
	          (succeeds (.containsInvokeExpr ?calledmethods))
	          (equals ?sootMDeclass (.getShortName (.getDeclaringClass (.getMethod (.getInvokeExpr ?calledmethods)))))
	          (equals false (= "StringBuilder" ?sootMDeclass))
	          (equals false (or  (IndexOfText ?sootMDeclass "aspectj")
	                             (IndexOfText ?sootMDeclass "apache")))
	          (equals false (= "<init>" (.getName (.getMethod (.getInvokeExpr ?calledmethods)))))
	          (equals false 
	                 (and
	                  (.startsWith (.toString (.getInvokeExpr ?calledmethods)) "staticinvoke")
	                  (IndexOfText (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "$")
	                  (.containsInvokeExpr (.getFirstNonIdentityStmt (.getActiveBody (.getMethod (.getValue (.getInvokeExprBox ?calledmethods))))))
	                  (or 
	                    (.startsWith (.name (.getMethodRef (.getValue (.getInvokeExprBox (.getFirstNonIdentityStmt (.getActiveBody (.getMethod (.getValue (.getInvokeExprBox ?calledmethods))))))))) "ajc$set")        
	                    (.startsWith (.name (.getMethodRef (.getValue (.getInvokeExprBox (.getFirstNonIdentityStmt (.getActiveBody (.getMethod (.getValue (.getInvokeExprBox ?calledmethods))))))))) "ajc$get"))))
	          (equals false 
	                (and
	                   (or
	                       (.startsWith (.toString (.getInvokeExpr ?calledmethods)) "staticinvoke")
	                       (.startsWith (.toString (.getInvokeExpr ?calledmethods)) "virtualinvoke"))
	                   (and (or
	                          (lastIndexOfText (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "$advice")
	                          (.startsWith (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$around$"))
	                   (false? (lastIndexOfText (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "proceed")))));counting proceed() method as a basic method call
	          (equals false
	                  (or 
	                    (= "aspectOf" (.getName (.getMethod (.getValue (.getInvokeExprBox ?calledmethods)))))
	                    (= "makeJP" (.getName (.getMethod (.getValue (.getInvokeExprBox ?calledmethods)))))))
	          (equals true (= "com.sun.j2ee.blueprints.util.aspect.XMLDocumentExceptionGenericAspect" (.getName (.getDeclaringClass ?soot|method))))))

 (inspect (ekeko [?soot|method ?advices ?calledmethods] (NOMethodCalls-perAdvice ?advices ?calledmethods ?soot|method)))
;COUNT
 ;(count (ekeko [?advices ?calledmethods] (NOMethodCalls-perAdvice ?advices ?calledmethods)))

 ;(equals false (or (= "PrintStream" ?shortname) (= "StringBuilder" ?shortname) )))))
 ;soot|value|invocation-soot|method
 ;soot.jimple.internal.JAssignStmt
 ;soot.jimple.internal.JInvokeStmt

 ;(inspect (ekeko [?method ?call]
 ;  (jsoot/soot|method-soot|unit ?method ?call))) ;?call : class soot.jimple.internal.JInvokeStmt,JInterfaceInvoke,JSpecialInvoke,JStaticInvoke ,JVirtualInvoke

 (inspect (ekeko [?c ?m]
  (jsoot/soot-class-method ?c ?m)
   (equals false (> (.indexOf (.getName ?c) "org.aspectj")-1))
   (equals false (> (.indexOf (.getName ?c) "org.apache")-1))))
;(inspect (ekeko [?t ?m]  (w/type-method ?t ?m)))
 ;(println "type-method")

 ;(inspect (ekeko [?caller ?callee ?call ?receiver]  ( method-methodCalls ?caller ?callee ?call ?receiver)))
 ;(inspect (ekeko [?advice ?unit] (advice-soot|unit ?advice ?unit)))

 ;################################## NOPointcuts ##################################

 ;aspect or class and its pointcut definitions
 ;(inspect (ekeko [?type ?pointdef] (w/type-pointcutdefinition ?type ?pointdef )))
 ;count aspects and its poincuts' definitions
 ;(inspect (ekeko [?aspects ?pointcuts] (w/aspect-pointcutdefinition ?aspects ?pointcuts )))

 ;get the all poincuts even the anoymous poincuts
 ;(inspect(ekeko [?point] (w/pointcut ?point)))

 ;just count the number of poincuts declared  properly in aspects 
 ;(inspect (ekeko [?point] (w/pointcutdefinition ?point)))

 ;pointcut and its primitive pointcuts; if there is no primitive pointcuts of a pointcut , the pointcut wont show
 ;(inspect (ekeko [?pointdef ?point] (w/pointcutdefinition-pointcut ?pointdef ?point)))

 ;(inspect (ekeko [?point] (ajdt/pointcut ?point)))

 ;advice & the definition of its pointcuts
 ;if it is not pointcut definion of the advice, the advice will not show off
 ;(inspect (ekeko [?advice ?pointdef] (w/advice-pointcutdefinition ?advice ?pointdef)))

 ;GET THE ADVICES that have EMPTY POINTCUTS
 (inspect (ekeko [?as ?ad]
                 (l/fresh [?point ?po]
                 (w/aspect-advice ?as ?ad)
                 (equals ?po (.getPointcut ?ad))
                 (equals  true (empty? (.toString  ?po))))))
;################################# SHADOWS #################################

 ;(inspect (ekeko [?shadow ?type] (w/shadow-ancestor|type ?shadow ?type)))

 ;(inspect (ekeko [?shadow ?class] (w/shadow-ancestor|class ?shadow ?class))) 
 ;(inspect (ekeko [?shadow ?aspect] (w/shadow-ancestor|aspect ?shadow ?aspect)))
 ;(inspect (ekeko [?shadow ?advice] (w/shadow-ancestor|advice ?shadow ?advice)))
;)

 (defn- 
   getDeclarationClassname  
   [?method ?decName]  
   (equals ?decName (.getName (.getDeclaringClass ?method))))

 (defn- 
   getInvokeExprBoxMethod
   [?method ?invokexprbox]
   (equals ?invokexprbox (.getMethod (.getValue (.getInvokeExprBox ?method)))))

 (defn- 
   getInvokeExprMethod
   [?method ?invokexp]
   (equals ?invokexp (.getMethod (.getValue (.getInvokeExpr ?method)))))

 (defn- 
   lastIndexOfText 
   [from to]  
   (> (.lastIndexOf from to) -1))

 (defn-  
   IndexOfText  
   [from to](> (.indexOf from to) -1))
 
