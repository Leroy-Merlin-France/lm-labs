<@extends src="/views/labs-common-base.ftl">

  <@block name="css">
    <@superBlock/>
    <link rel="stylesheet/less" media="all" href="${Context.modulePath}/@views/variables.less" />
        <style type="text/css">
      label {
      font-weight: bold;
      }
    </style>
  </@block>

  <@block name="scripts">
    <script type="text/javascript" src="/nuxeo/wro/labs.manage.js"></script>
  </@block>

  <@block name="topbar">
    <@superBlock/>
    <a href="${Context.modulePath}"><img class="ribbon" src="${skinPath}/images/beta-ribbon.png" alt="Labs-beta"></a>
  </@block>

  <@block name="FKtopContent">
    <div id="masthead">
          <img src="${skinPath}/images/default_banner.png" />
      </div>

      <div class="container" style="width: 960px;">
      <#--  action-message -->
      <#if This.type.superType.name == "LabsPage" >
      <#include "views/common/action_message.ftl" >
      </#if>
        <@block name="content" />
      </div>
  </@block>

  <@block name="FKfooter">
    <div id="FKfooter">
      <#include "views/footer-manage-base.ftl">
          <#include "views/common/loading.ftl">
      </div><#-- /FKfooter -->
  </@block>

</@extends>
