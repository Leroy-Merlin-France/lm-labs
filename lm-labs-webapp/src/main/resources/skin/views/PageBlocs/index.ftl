<html>
  <head>
    <title>${siteName}</title>
    <script type="text/javascript" src="${skinPath}/js/jquery/jquery-1.5.1.min.js"></script>
    <script type="text/javascript" src="${skinPath}/js/jquery/jquery.form.js"></script>
	<script type="text/javascript" src="${skinPath}/js/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="${skinPath}/js/ckeditor/init.js"></script>
    
    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/labssite.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/page_blocs.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/wysiwyg_editor.css"/>
	<link rel="stylesheet" type="text/css" href="${skinPath}/css/ckeditor.css"/>
  </head>
  <body>
    <div id="header">
    HEADER
    </div>
    <div id="content" class="pageBlocs">
      <#-- SIDEBAR AREA --> 
      <#include "views/common/sidebar_area.ftl" />
      
      <#-- COMMENT AREA --> 
      <#include "views/common/comment_area.ftl" />
      
      <#assign i=1>
      <#list rootFolder as root>
        <div id="bloc${i}" class="bloc">
          ${root.title}
          <#list Session.getChildren(root.ref) as child>
            ${child.title}
          </#list>
        </div>
        <#assign i=i+1>
      </#list>
      <#-- FIXME -->
      <div id="bloc${i}" class="bloc">
        MANUTE FIXME
      </div>
    </div>
    <div id="footer">
      FOOTER
    </div>
  </body>
</html>