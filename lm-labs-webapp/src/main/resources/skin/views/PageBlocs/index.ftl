<html>
  <head>
    <title>${siteName}</title>
    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/labssite.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/page_blocs.css"/>
  </head>
  <body>
    <div id="header">
    HEADER
    </div>
    <div id="content" class="pageBlocs">
      <div id="sidebar">
        SIDEBAR
      </div>
      <div id="comment">
      	Bienvenue sur le site '${This.name}'
      	<br />
      	${This.description}${This.desc}
      </div>
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