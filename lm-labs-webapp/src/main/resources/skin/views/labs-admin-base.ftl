<@extends src="/views/labs-common-base.ftl">

  <@block name="title">${Common.siteDoc(Document).getSite().title} - Administration</@block>

  <@block name="css">
    <@superBlock/>
        <link rel="stylesheet" type="text/css" href="${skinPath}/css/ckeditor.css"/>
        <link rel="stylesheet" href="${Context.modulePath}/${mySite.URL}/@currenttheme/rendercss-${mySite.themeManager.getTheme(Session).document['dc:modified']?string("yyyyMMddHHmmss")}?withoutaddedstyle=true" />
  </@block>

  <@block name="scripts">
    <@superBlock/>
            <script type="text/javascript" src="${skinPath}/js/ckeditor/init.js"></script>
            <script type="text/javascript" src="${skinPath}/js/ckeip.js"></script>
            <script type="text/javascript" src="${skinPath}/js/ckeditor/ckeditor.js"></script>
            <script type="text/javascript" src="${skinPath}/js/ckeditor/adapters/jquery.js"></script>
            <script type="text/javascript" src="${skinPath}/js/tooltip.js"></script>
            <script type="text/javascript" src="${skinPath}/js/jquery/jquery.hotkeys.js"></script>
            <script type="text/javascript" src="${skinPath}/js/jquery/jquery.filedrop.js"></script>
            <script type="text/javascript" src="${skinPath}/js/asset_filedrop.js"></script>
            <script type="text/javascript" src="${skinPath}/js/jquery.ThreeDots.min.leroymerlin.js"></script>
            <script type="text/javascript" src="${skinPath}/js/bootstrap/bootstrap-tab.js"></script>
  </@block>

  <@block name="topbar" />

  <@block name="FKtopContent">
    <style type="text/css">
        #FKtopContent {
          margin-top: 0px;
          padding-top: 0px;
          width: 980px;
        }
        .container{
          width: 960px;
        }
        .breadcrumb{
          margin-top: 3px;
        }
    </style>

      <div class="container" style="width: 980px;">
        <#include "views/common/topbar.ftl" />
        <#if Context.principal.isAdministrator() == true>
        <a href="${Context.modulePath}"><img class="ribbon" src="${skinPath}/images/beta-ribbon.png" alt="Labs-beta"></a>
      </#if>
    </div>
    <div class="container">
        <@block name="breadcrumbs">
          <#include "views/common/breadcrumbs_siteadmin.ftl" >
        </@block>
        <@block name="tabs" />
        <#--  action-message -->
      <#include "views/common/action_message.ftl" >
        <@block name="content" />
      </div>
  </@block>

  <@block name="pageCommentable" />

  <@block name="FKfooter">
    <div id="FKfooter">
      <#include "views/common/footer.ftl">
          <#include "views/common/loading.ftl">
      </div><#-- /FKfooter -->
  </@block>

</@extends>
