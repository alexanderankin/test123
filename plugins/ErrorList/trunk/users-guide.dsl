<!DOCTYPE style-sheet PUBLIC "-//James Clark//DTD DSSSL Style Sheet//EN" [
<!ENTITY dbstyle PUBLIC "-//Norman Walsh//DOCUMENT DocBook HTML Stylesheet//EN"
CDATA DSSSL> ]>

<style-sheet>
<style-specification use="html">
<style-specification-body>

(define %html-ext% ".html")
(define %shade-verbatim% #t)
(define %root-filename% "users-guide")
(define %use-id-as-filename% #t)
(define %body-attr%
	(list
		(list "BGCOLOR" "#FFFFFF")))

(define %admon-graphics% #f)
(define %spacing-paras% #f)
(define %html-manifest% #t)

;; make these pretty
(element guibutton ($bold-seq$))
(element guimenu ($bold-seq$))
(element guimenuitem ($bold-seq$))
(element guisubmenu ($bold-seq$))
(element application ($mono-seq$))
(element glossterm ($bold-seq$))
(element (funcdef function) ($bold-seq$))
(element funcsynopsis (process-children))

(define %funcsynopsis-style% #f)

;; abstract is used to tag data, not to render output
(element (listitem abstract) (process-children))

;; workaround for stupid Swing HTML limitation - it can't display
;; DocBook's quotes properly

(element quote
	(make sequence
		(literal "\"")
		(process-children)
		(literal "\"")))

;; Swing HTML doesn't support tables properly

(define %gentext-nav-use-tables% #f)

;; another Swing HTML fix: "WIDTH" hard-coded here at 100%

(element (listitem informalexample)
  (let ((id (element-id))
  	(rule-before? %informalexample-rules%)
	(rule-after? %informalexample-rules%))
    (make element gi: "DIV"
	  attributes: (list
			(list "BORDER" "0")
			(list "BGCOLOR" "#E0E0E0")
			(list "WIDTH" "100%"))
	  (if id
	      (make element gi: "A"
		    attributes: (list (list "NAME" id))
		    (empty-sosofo))
	      (empty-sosofo))

	  (if %spacing-paras%
	      (make element gi: "P" (empty-sosofo))
	      (empty-sosofo))

	  (if rule-before?
	      (make empty-element gi: "HR")
	      (empty-sosofo))

	  (process-children)

	  (if rule-after?
	      (make empty-element gi: "HR")
	      (empty-sosofo))

	  (if %spacing-paras%
	      (make element gi: "P" (empty-sosofo))
	      (empty-sosofo)))))

</style-specification-body>
</style-specification>
<external-specification id="html" document="dbstyle">
</style-sheet>
