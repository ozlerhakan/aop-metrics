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
     SuperFieldAccess FieldAccess ConstructorInvocation ASTNode ASTNode$NodeList CompilationUnit]))

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
(defn class-loc [filePath]
  (reduce
    +
    (for [file (file-seq filePath) :when (.endsWith (.toString file )".java")]
      (with-open [rdr (io/reader file)] (count  (filter #(re-find #"\S" %) (line-seq rdr)))))))

;(class-loc (io/file "C:/Users/HAKAN/runtime-New_configuration-clojure/Clojure-1/src/"))
(class-loc (io/file "C:/Users/HAKAN/Desktop/Thesis/ws/AJHotDraw/src/aspects"));36225

;##################################### METRIC LOC ###################################
;count the number of lines of code in a given project
(defn aspect-loc [filePath]
  (reduce
    +
    (for [file (file-seq filePath) :when (.endsWith (.toString file )".aj")]
      (with-open [rdr (io/reader file)] (count  (filter #(re-find #"\S" %) (line-seq rdr)))))))

;(aspect-loc (io/file "C:/Users/HAKAN/runtime-New_configuration-clojure/Clojure-1/src/"))
(aspect-loc (io/file "C:/Users/HAKAN/Desktop/Thesis/ws/AJHotDraw/src/aspects"));2111

;#################################### METRIC VS ##################################### PASS
;the number of JAVA classes (.java) in a selected project
(defn class-count [filePath]
  (reduce
    +
    (for [file (file-seq filePath) :when (.endsWith (.toString file ) ".java")] 1) ))
(class-count (io/file "C:/Users/HAKAN/Desktop/Thesis/ws/AJHotDraw/src/aspects"));

;Number of Classes in the project except interfaces and sub-classes!!
(inspect (ekeko [?classes]
                 (w/class ?classes) 
                 (equals false (or 
                                 (> (.indexOf  (.getName ?classes) "$") -1)
                                 (> (.indexOf  (.getName ?classes) "lang.Object") -1)))))

;the number of aspects in a selected project
(count (ekeko [?aspects]  (w/aspect ?aspects)));

;number of aspectj (.aj) in a project! 
(defn aspect-count [filePath]
  (reduce
    +
    (for [file (file-seq filePath) :when (.endsWith (.toString file )".aj")] 1) ))
(aspect-count (io/file "C:/Users/HAKAN/Desktop/Thesis/ws/AJHotDraw/src/aspects"));

;########################## METRIC NOAttributes-fields ############################# 
;FIELD
;(inspect (count (ekeko [?fields] (w/field ?fields))))
;(inspect  (ekeko [?fields] (w/field ?fields))); get regular from weaver

;only works for aspects to count the  number of fields!
(inspect (ekeko [?fields] (ajdt/field ?fields)))
;(count (ekeko [?fields] (ajdt/field ?fields )));Relation of fields declared within  aspects

;count-fields-in both CLASSES except their subclass' fields and ASPECTS !!
(inspect (ekeko [?c ?f]
                (jsoot/soot-class-field ?c ?f)
                (equals false (or  
                                (> (.indexOf  (.getName ?c) "aspectj") -1)
                                (> (.indexOf  (.getName ?c) "$") -1)))
                (equals true (empty? (filter #(re-matches #"\S+\$[1-9]+" %)  [(.getName ?c)])))
                (equals false (or 
                                (.startsWith (.getName ?f) "ajc")
                                (.startsWith (.getName ?f) "$")
                                (.startsWith (.getName ?f) "this")))))

;#################################### METRIC NOO ###################################
;METHODS: both all classes and aspects
;(inspect (ekeko [?methods] (w/method ?methods)))

;NUMBER OF METHODS: only responsible for classes! 
(defn classes-methods [?types ?methods]
                (l/fresh []
                         (w/type-method ?types ?methods)
                         (equals false 
                                 (or 
                                   (= "java.lang.Object" (.getName ?types)) 
                                   (.isAspect ?types)))
                         (equals false (= "STATIC_INITIALIZATION" (.toString (.getKind ?methods))))
                         (equals false (.isInterface ?methods))
                         (equals true  (empty? (filter #(re-matches #"\S+\$[1-9]+" %)  [(.getName (.getDeclaringType ?methods))])))
                         (equals true  (empty? (filter #(re-matches #"\S+\$\S+" %)  [(.getName  ?types)])))))
;1 Class 
(inspect (ekeko [?types ?mths] (classes-methods ?types ?mths)))
;(inspect (count ( ekeko [get] (classes-methods get))))

;2 Aspect : find basic methods in aspect files
(inspect  (ekeko [?method-aspect] (ajdt/method ?method-aspect)))

; <= without intertyped method declarations
;----------------------------------------------
;    intertyped method declarations =>

;METHODS: only intertyped method declarations in aspect files
;1 Aspect
(defn aspects-intertyped-methods [?methods]
                (l/fresh [?types]
                         (w/type-method ?types ?methods)
                         (equals false  (or 
                                           (= "STATIC_INITIALIZATION" (.toString (.getKind ?methods))) 
                                           (= "CONSTRUCTOR" (.toString (.getKind ?methods)))))
                         (equals true   (.isAspect ?types))
                         (equals false  (.isAjSynthetic ?methods))
                         (equals false  (or 
                                          (= "hasAspect" (.getName ?methods)) 
                                          (= "<init>" (.getName ?methods))))
                         (equals false  (.isInterface ?methods))
                         (equals true   (.startsWith (.getName ?methods) "ajc$interMethod$"))));$AFTER,$BEFORE, so on...

(inspect (ekeko [?get] (aspects-intertyped-methods ?get)))
;(inspect  (ekeko [?method-aspect] (ajdt/method ?method-aspect)))

;COUNT: 1 class + 2 aspect + 1 aspect :RESULT combines with 

;(inspect (count (ekeko [?method-aspect] (ajdt/method ?method-aspect))))
;----------------------------------------------
;NUMBER OF ADVICES: only for aspects
(defn NOFA-in-aspects [?adv] (w/advice ?adv))
(inspect (ekeko [?adv] (NOFA-in-aspects ?adv)));include softener!! coution!
(inspect (ekeko [?softener] (w/declare|soft ?softener)));in order to delete softener parts from the NOFA-in-aspects , use this query!

;(inspect (count (ekeko [?adv] (NOFA-in-aspects ?adv)))) 

;############# Advice-Method dependence (AM) :the number of method calls per advice body #######

;(inspect (ekeko [?advices] (w/advice ?advices)))
;(inspect  (ekeko [?advices] (ajdt/advice ?advices)))

;Additional query: get the entire soot methods of the selected advice
(defn get-advices-soot|methods [?soot|advicemethod ?units ?package]
               (l/fresh [?adv]
                         (ajsoot/advice-soot|method ?adv ?soot|advicemethod)
                         (equals ?units (.getUnits (.getActiveBody ?soot|advicemethod)))
                         (equals true (=  ?package (.getName (.getDeclaringClass ?soot|advicemethod))))))

(inspect (ekeko [?soot|advicemethod ?units]
                (get-advices-soot|methods ?soot|advicemethod ?units "org.jhotdraw.ccconcerns.commands.CommandObserver")))
;isJavaLibraryMethod();12-> 11 oldu

;################################## WORKING!! (I hope so) ####################################
(defn NOMethodCalls-perAdvice [?advices ?calledmethods ?soot|method]; ?method : class soot.SootMethod
          (l/fresh []
          (ajsoot/advice-soot|method ?advices ?soot|method)
          (jsoot/soot|method-soot|unit ?soot|method ?calledmethods)
          (equals true (or (= "soot.jimple.internal.JAssignStmt" (.getName (.getClass ?calledmethods)))  
                           (= "soot.jimple.internal.JInvokeStmt" (.getName (.getClass ?calledmethods)))))
          (equals true (.containsInvokeExpr ?calledmethods))
          (equals false (= "StringBuilder" (.getShortName (.getDeclaringClass (.getMethod (.getInvokeExpr ?calledmethods))))))
          (equals false (= "<init>" (.getName (.getMethod (.getInvokeExpr ?calledmethods)))))
          (equals false 
                 (and
                  (.startsWith (.toString (.getInvokeExpr ?calledmethods)) "staticinvoke")
                  (> (.indexOf (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "$") -1)
                  (.containsInvokeExpr (.getFirstNonIdentityStmt (.getActiveBody (.getMethod (.getValue (.getInvokeExprBox ?calledmethods))))))
                  (or 
                    (.startsWith (.name (.getMethodRef (.getValue (.getInvokeExprBox (.getFirstNonIdentityStmt (.getActiveBody (.getMethod (.getValue (.getInvokeExprBox ?calledmethods))))))))) "ajc$set")        
                    (.startsWith (.name (.getMethodRef (.getValue (.getInvokeExprBox (.getFirstNonIdentityStmt (.getActiveBody (.getMethod (.getValue (.getInvokeExprBox ?calledmethods))))))))) "ajc$get")
                  )))
          (equals false 
                  (and
                    (or
                      (.startsWith (.toString (.getInvokeExpr ?calledmethods)) "staticinvoke")
                      (.startsWith (.toString (.getInvokeExpr ?calledmethods)) "virtualinvoke"))
                    (and (or
                           (> (.lastIndexOf (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "$advice") -1)
                           (.startsWith (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "ajc$around$"))
                         (false? (> (.lastIndexOf (.getName (.getMethod (.getInvokeExpr ?calledmethods))) "proceed") -1)))));counting proceed() method as a basic method call
          (equals false 
                  (or 
                    (= "aspectOf" (.getName (.getMethod (.getValue (.getInvokeExprBox ?calledmethods)))))
                    (= "makeJP" (.getName (.getMethod (.getValue (.getInvokeExprBox ?calledmethods)))))))
          (equals true (= "org.jhotdraw.ccconcerns.commands.CommandObserver" (.getName (.getDeclaringClass ?soot|method))))))

(inspect (ekeko [?advices ?calledmethods ?soot|method] (NOMethodCalls-perAdvice ?advices ?calledmethods ?soot|method)))
;COUNT
;(inspect (count (ekeko [?advices ?calledmethods] (NOMethodCalls-perAdvice ?advices ?calledmethods))))

;(equals false (or (= "PrintStream" ?shortname) (= "StringBuilder" ?shortname) )))))
;soot|value|invocation-soot|method
;soot.jimple.internal.JAssignStmt
;soot.jimple.internal.JInvokeStmt
;(inspect (ekeko [?method ?call]
;  (jsoot/soot|method-soot|unit ?method ?call)));?call : class soot.jimple.internal.JInvokeStmt,JInterfaceInvoke,JSpecialInvoke,JStaticInvoke ,JVirtualInvoke

;(inspect (ekeko [?caller ?callee ?call ?receiver]  ( method-methodCalls ?caller ?callee ?call ?receiver)))
;(inspect (ekeko [?advice ?unit] (advice-soot|unit ?advice ?unit)))

;################################## NOPointcuts ##################################

;aspect or class and its pointcut definitions
;(inspect (ekeko [?type ?pointdef] (w/type-pointcutdefinition ?type ?pointdef )))
;count aspects and its poincuts' definitions
;(inspect (ekeko [?aspects ?pointcuts] (w/aspect-pointcutdefinition ?aspects ?pointcuts )))

;get the all poincuts even the anouymous poincuts
;(inspect(ekeko [?point] (w/pointcut ?point)))

;just count the number of poincuts declared  properly in aspects 
;(inspect (ekeko [?point] (w/pointcutdefinition ?point)))

;pointcut and its primitive poincuts
;(inspect (ekeko [?pointdef ?point] (w/pointcutdefinition-pointcut ?pointdef ?point)))
;(inspect (ekeko [?point] (ajdt/pointcut ?point)))

;advice & the definition of its pointcut
;(inspect (ekeko [?advice ?pointdef] (w/advice-pointcutdefinition ?advice ?pointdef)))
;(inspect (ekeko [?adv ?point] (w/advice-pointcut ?adv ?point)))

;################################# SHADOWS #################################

;(inspect (ekeko [?shadow ?type] (w/shadow-ancestor|type ?shadow ?type)))

;(inspect (ekeko [?shadow ?class] (w/shadow-ancestor|class ?shadow ?class))) 
;(inspect (ekeko [?shadow ?aspect] (w/shadow-ancestor|aspect ?shadow ?aspect)))
;(inspect (ekeko [?shadow ?advice] (w/shadow-ancestor|advice ?shadow ?advice)))

;);COMMENT last SCOPE