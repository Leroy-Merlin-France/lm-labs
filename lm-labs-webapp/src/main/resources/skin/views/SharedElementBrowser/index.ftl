<@extends src="/views/labs-common-base.ftl">
<#import "libs/LabsUtils.ftl" as LabsUtils />

<#assign adminTreeviewType = "Pages" />
<#assign mySite=Common.siteDoc(Document).site />
<#assign parentIds = LabsUtils.getHomePageDocIdSelectorsStr(Document) />

<@block name="title">${mySite.title} - Sélection Eléments</@block>

<@block name="css">
    <@superBlock/>
    <link rel="stylesheet" type="text/css" href="${contextPath}/wro/labs.common.css" />
  <link rel="stylesheet" href="${Context.modulePath}/${mySite.URL}/@currenttheme/rendercss-${mySite.themeManager.getTheme(Session).document['dc:modified']?string("yyyyMMddHHmmss")}" />
</@block>

<@block name="scripts">
  <@superBlock/>
  <script type="text/javascript" src="${contextPath}/wro/labs.common.js"></script>
  <script type="text/javascript" src="${skinPath}/js/jstree/jquery.jstree.js"></script>
  <script type="text/javascript" >
<#include "common/jstree-icons-labels-js.ftl" >
<#include "macros/jstree-controls.ftl" />

var selectedUrl = '';

function sendToCallFunction(data, href) {
  window.opener.${This.activeAdapter.getCallFunction()}('${This.activeAdapter.getCalledRef()}', href<#if This.activeAdapter.getCallFunction() != "CKEDITOR.tools.callFunction" >, jQuery(data.rslt.obj).attr("id")</#if>);
    window.close();
}

jQuery(document).ready(function() {
  jQuery("#jstree")
  .bind("loaded.jstree", function (event, data) {
    changeIconOfHomePage();
    addStatusLabels(data);
  })
  .bind("before.jstree", function (e, data) {
<#--
    //console.log('before.jstree ' + data.func + ' ' + data.args.length);
-->
    if(data.func === "reload_nodes") {
      changeIconOfHomePage();
      addStatusLabels(data);
    } else if(data.func === "after_open") {
      addNodesStatusLabels(data);
    }
  })
  .bind("select_node.jstree", function (e, data) {
    var type = jQuery(data.rslt.obj).attr("rel");
    if (type != "Folder" && type != "Tree" && type != "Site" && type != "Assets"){
      selectedUrl = '${Context.modulePath}/' + data.rslt.obj.data("url");
      sendToCallFunction(data, selectedUrl);
    }
    else{
      alert('${Context.getMessage('label.sharedelement.noselect.element')?js_string}');
    }
  })
  .jstree({
    "core": {
      "html_titles" : true,
      "strings": { loading : "${Context.getMessage('label.admin.loading')} ..." }
      , "initially_open" : ${parentIds}
    },
    "themes" : {
            "theme" : "classic"
        },
    "types" : {
      "valid_children" : [ "Site" ],
      "types" : {
        "Site" : {
          "remove" : false,
          "rename" : false,
          "icon" : {
            "image" : "/nuxeo/icons/site.jpeg"
          }
        },
        "Tree" : {
          "remove" : false,
          "rename" : false,
          "icon" : {
            "image" : "/nuxeo/icons/folder_template.gif"
          }
        },
        "PageForum" : {
          "icon" : {
            "image" : "/nuxeo/icons/forum.gif"
          }
        },
        "LMForumTopic" : {
          "icon" : {
            "image" : "/nuxeo/icons/forum_thread.gif"
          }
        },
        "LabsNews" : {
          "icon" : {
            "image" : "/nuxeo/icons/news.png"
          }
        },
        "File" : {
          "icon" : {
            "image" : "/nuxeo/icons/file.gif",
            "position" : "0px 0px"
          }
        },
        "Folder" : {
          "icon" : {
            "image" : "/nuxeo/icons/folder.gif",
            "position" : "0px 0px"
          }
        },
        "Picture" : {
          "icon" : {
            "image" : "/nuxeo/icons/picture.gif"
          }
        },
        "Assets" : {
          "icon" : {
            "image" : "/nuxeo/icons/picturebook.gif"
          }
        },
        "default" : {
          "icon" : {
            "image" : "/nuxeo/icons/page_text.gif",
            "position" : "0px 0px"
          }
        }
      }
    },
    "json_data" : {
      "ajax" : {
        "url" : "${Root.getLink(mySite.document)}/@sharedElementTreeview"
        , "data" : function (n) {
          return {
            "view" : "${This.activeAdapter.viewMode}",
            "id" : n.attr ? n.attr("id") : 0
          };
        }
        //, "error" : function(jqXHR, textStatus, errorThrown) { alert("ERROR: " + jqXHR.status + ", " + jqXHR.statusText + ", " + textStatus + ", "+ errorThrown);}
      }
    },
    "plugins" : [ "json_data", "themes", "ui", "types"
    ]
  }); <#-- jQuery.jstree() -->
});
  </script>
</@block>

<@block name="topbar" />

  <div id="FKtopContent">
    <@block name="FKtopContent">

      <div class="container">
        <div class="row"><div class="span11 well">
        <strong>Veuillez sélectionner un élément en cliquant sur son titre.</strong>
        </div></div>
        <div class="row"><div class="span12">
          <@jsTreeControls treeId="jstree" />
        </div></div>
        <div class="row">
          <div class="span12" style="margin-top:10px;margin-bottom:10px">
            <div id="jstree"></div>
          </div>
        </div><#-- row -->
        <div class="row"><div class="span12">
          <@jsTreeControls treeId="jstree" />
        </div></div>
      </div><#-- container -->
    </@block>
  </div>

<@block name="FKfooter" />

<@block name="bottom-page-js" />

</@extends>
