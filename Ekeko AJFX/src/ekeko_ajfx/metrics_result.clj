(ns ekeko-ajfx.metrics-result
  (require [ekeko-ajfx.AOPMetrics :as metrics]
           [clojure.core.logic :as l])
  (use [inspector-jay.core]
        [clojure.repl]
        [damp.ekeko logic]
        [damp.ekeko ekekomodel]
        [damp.ekeko gui]
        [damp.ekeko]
        [clojure.inspector :exclude [inspect]]))

;Disable the comment block so that the metrics can be run respectively. -line:13 to 230
(comment 

;############################### METRIC LOC ###############################

(metrics/LOC ".../AspectJProjectDirectory/src" "MainTST")

;############################### METRIC VS ############################### 

(inspect  (sort-by first (ekeko [?classname] (l/fresh [?class] (metrics/NOClasses ?class) (equals ?classname (.getName ?class))))))
(inspect (sort-by first (ekeko [?asName] (l/fresh [?aspect ?source] (metrics/NOAspects ?aspect ?source) (equals ?asName (.getName ?aspect))))))

(metrics/VSize)

;############################### METRIC NOAttributes (fields) ###############################

(inspect (sort-by first (ekeko [?modules ?field] (l/fresh [?module] (metrics/NOA ?module ?field ) (equals ?modules (.getName (first ?module)))))))
(count (ekeko [?module ?field] (metrics/NOA ?module ?field))) 

;############################### METRIC NOOperations (methods and advices) ###############################

(inspect (sort-by first (ekeko [?module ?method] (l/fresh[?m] (metrics/NOM ?m ?method) (equals ?module (.getName (first ?m)))))))
(count ( ekeko [?module ?method] (metrics/NOM ?module ?method)))

;############################### METRIC NOIntertype methods ############################### 

(inspect (ekeko [?i] (metrics/NOI ?i)))
(count (ekeko [?i] (metrics/NOI ?i)))

;############################### METRIC NOAdvices ###############################

(inspect (sort-by first (ekeko [?asStr ?pointcut ?advice] (l/fresh [?aspect] (metrics/NOAdvices ?aspect ?advice ?pointcut) (equals ?asStr (.toString ?aspect))))))

;############################### Advice-Method dependence (AM) ###############################

(inspect (sort-by first (ekeko [?aspectName ?calledmethods ?soot|methodName] (metrics/AM ?aspectName ?calledmethods ?soot|methodName))))
(count (sort-by first (ekeko [?aspectName ?calledmethods ?soot|methodName] (metrics/AM ?aspectName ?calledmethods ?soot|methodName)))) 

;############################### IntertypeMethod-Method dependence (IM) ###############################

(inspect (sort-by first (ekeko [ ?aspectName ?calledmethods ?soot|interName ] (metrics/IM  ?aspectName ?calledmethods ?soot|interName ))))
(count (ekeko [ ?aspectName ?calledmethods ?soot|interName ] (metrics/IM  ?aspectName ?calledmethods ?soot|interName )))

;############################### Method-Method dependence (MM) ###############################

(inspect (sort-by first (ekeko [ ?aspectName  ?soot|methodName ?calledmethods]   (metrics/MM ?aspectName ?calledmethods ?soot|methodName))))
(count (ekeko [ ?aspectName  ?soot|methodName ?calledmethods] (metrics/MM  ?aspectName ?calledmethods ?soot|methodName)))

;############################### Attribute-Class dependence (AtC) ###############################
  
(inspect (sort-by first (ekeko [?typef ?f ?t] (metrics/AtC ?t ?f ?typef))))
(count (ekeko [?typef ?f ?t] (metrics/AtC ?t ?f ?typef)))

;############################### Advice-Class  dependence (AC) ###############################
  
;combined the two queries in one inspect
(inspect
  (sort-by first (clojure.set/union
                   (ekeko [?typename  ?aspectSN ?adviceKind] (metrics/AC-p1 ?aspectSN ?adviceKind ?typename))
                   (ekeko [?typename  ?aspectSN ?adviceKind] (metrics/AC-p2 ?aspectSN ?adviceKind ?typename)))))
  
(count  (clojure.set/union
          (ekeko [?typename  ?aspectSN ?adviceKind] (metrics/AC-p1 ?aspectSN ?adviceKind ?typename))
          (ekeko [?typename  ?aspectSN ?adviceKind] (metrics/AC-p2 ?aspectSN ?adviceKind ?typename))))


;############################### Intertype method-Class dependence (IC) ###############################
  
;combined the two queries in one inspect
(inspect 
  (sort-by first  (clojure.set/union
                    (ekeko [?typename ?aspect ?interName ?type] (metrics/IC-returnType ?aspect ?interName ?type ?typename ))
                    (ekeko [?typename ?aspect ?interName ?type] (metrics/IC-parameters ?aspect ?interName ?type ?typename )))))

(count (clojure.set/union
         (ekeko [?typename ?aspect ?interName ?type] (metrics/IC-returnType ?aspect ?interName ?type ?typename))
         (ekeko [?typename ?aspect ?interName ?type] (metrics/IC-parameters ?aspect ?interName ?type ?typename))))

;############################### Method-Class dependence (MC) ###############################
  
;combined the two queries in one inspect
(inspect
  (sort-by first
           (clojure.set/union
             (ekeko [?paramName ?aspectn ?methodN ?rtype] (metrics/MC-param ?aspectn ?methodN ?paramName ?rtype))
             (ekeko [?paramName ?aspectn ?methodN ?rtype] (metrics/MC-return ?aspectn ?methodN ?paramName ?rtype)))))

 (count (clojure.set/union
                           (ekeko [?paramName ?aspectn ?methodN ?rtype] (metrics/MC-param ?aspectn ?methodN ?paramName ?rtype))
                           (ekeko [?paramName ?aspectn ?methodN ?rtype] (metrics/MC-return ?aspectn ?methodN ?paramName ?rtype))))

;############################### Pointcut-Class dependence (PC) ###############################
  
(inspect (sort-by first (ekeko [?typename ?pointcutname ?aspect] (metrics/PC ?typename ?pointcutname ?aspect))))
(count (ekeko [?typename ?pointcutname ?aspect] (metrics/PC ?typename ?pointcutname ?aspect)))
  
;############################### Pointcut-Method dependence (PM) ###############################
  
(inspect (sort-by first  (ekeko [?CalledM  ?calledC ?aspectName ?adviceKind ?pointcut ?shadow] (metrics/PM ?calledC ?CalledM ?aspectName ?adviceKind ?pointcut ?shadow ))))
(count (ekeko [?CalledM  ?calledC  ?aspectName ?adviceKind ?pointcut ?shadow] (metrics/PM ?calledC ?CalledM ?aspectName ?adviceKind ?pointcut ?shadow))

;############################### Advice-Advance Pointcut Dependence (AAP) ###############################

(inspect  (sort-by first (ekeko [?shortAspect ?advicekind ?list ?pointdefs](metrics/NOAAdvice ?shortAspect ?advicekind ?pointdefs ?list))))
(count (ekeko [?shortAspect ?advicekind ?list ?pointdefs] (metrics/NOAAdvice ?shortAspect ?advicekind ?pointdefs ?list)))
  

;############################### Advice-Basic Pointcut Dependence (ABP) ###############################

(ekeko [?size ] (metrics/NOBAdvices ?size))

;############################### Inherited Aspects (InA) ###############################

(inspect (sort-by first (ekeko [?aspectname ?abstract] (l/fresh[?aspect] (metrics/InA ?aspect ?abstract) (equals ?aspectname (.getName ?aspect))))))
(count (ekeko [?aspectname ?name] (metrics/InA ?aspectname ?name)))
  
;############################### Singleton Aspects (SA) ###############################

(inspect (ekeko [?aspect ?association] (metrics/SA ?aspect ?association)))
(count (ekeko [?aspect ?association] (metrics/SA ?aspect ?association))) 
  
;############################### Non-Singleton Aspects (nSA) ###############################

(inspect (ekeko [?aspect ?association] (metrics/nSA ?aspect ?association)))
(count (ekeko [?aspect ?association] (metrics/nSA ?aspect ?association)))
  
;############################### Number of AdviceExecution (AE) ###############################

(inspect  (sort-by first (ekeko [?shortAspect ?nameaspect ?list ?pointdefs] (metrics/AE ?shortAspect ?nameaspect ?pointdefs ?list)))) 
(count (ekeko [?shortAspect ?nameaspect ?list ?pointdefs]  (metrics/AE ?shortAspect ?nameaspect ?pointdefs ?list)))

  
;############################### Advice-Join Point Shadow Dependence (AJ) ###############################

(metrics/AJ)

;############################### Argument size of Args-Advice (AAd) ###############################

(metrics/AAd)

;############################### Argument size of Args-Advice-args (AAda) ###############################

(metrics/AAda)

;############################### Number of Around Advice (NOAr) ###############################

(metrics/NOAr)

;############################### Number of Before/After Advice (NOBA) ###############################

(metrics/NOBA)

;############################### Number of After Throwing/Returning Advice (NOTR) ###############################

(metrics/NOTR)

;############################### Around Advice - non-Proceed Call Dependence (AnP) ###############################

(metrics/AnP)

;############################### Number of Advised Classes (AdC) ###############################

(metrics/AdC)

;############################### Number of non-Advised Classes (nAdC) ###############################

(metrics/nAdC)

;############################### Number of Call (NOC) ###############################

(inspect (sort-by first (ekeko [?shortAspect ?call ?advicekind] (metrics/NOC ?shortAspect ?call ?advicekind))))
(count (ekeko [?shortAspect ?call ?advicekind] (metrics/NOC ?shortAspect ?call ?advicekind)))
  
;############################### Number of Execution (NOE) ###############################

(inspect (sort-by first (ekeko [?shortAspect ?execution ?advicekind] (metrics/NOE ?shortAspect ?execution ?advicekind))))
(count (ekeko [?shortAspect ?execution ?advicekind] (metrics/NOE ?shortAspect ?execution ?advicekind)))
  
;############################### Number of Advised Methods (AdM) ###############################

(metrics/AdM)
(count (metrics/AdM))
  
;############################### Number of non-Advised Methods (nAdM) ###############################

(metrics/nAdM)

;############################### Classes and Subclasses (CsC) ###############################

(inspect (ekeko [?class ?subclasses] (metrics/NOCsC ?class ?subclasses)))
(count (ekeko [?class ?subclasses] (metrics/NOCsC ?class ?subclasses)))
  
;############################### Average of Subclasses of Classes (ScC) ###############################

(metrics/ScC)

;############################### Number of Wildcards (NOW) ###############################

(metrics/NOW)

;############################### Number of non-Wildcards (NOnW) ###############################

(metrics/NOnW)

;############################### Number of thisJoinPoint/Static (tJPS) ###############################

(inspect (sort-by first (into #{} (ekeko [?DC|advicemethod ?sootname ?soot|advicemethod] (metrics/tJPS ?DC|advicemethod ?sootname ?soot|advicemethod)))))
(count (into #{} (ekeko [?DC|advicemethod ?sootname ?soot|advicemethod] (metrics/tJPS ?DC|advicemethod ?sootname ?soot|advicemethod))))
  
;############################### Number of Accessed Args (AcA) ###############################

(metrics/AcA)

;############################### Number of Modified Args (MoA) ###############################

(inspect  (sort-by first (ekeko [?return ?parameters ?arg ?unit ?istrue] (metrics/NOModifying ?return ?parameters ?arg ?unit ?istrue))))
(count (ekeko [?return ?parameters ?arg ?unit ?istrue] (metrics/NOModifying ?return ?parameters ?arg ?unit ?istrue)))
 
)