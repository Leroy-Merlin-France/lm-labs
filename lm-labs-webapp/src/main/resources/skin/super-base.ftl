<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
    "http://www.w3.org/TR/html4/strict.dtd">
<html lang="fr">
    <head>
        <@block name="meta">
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <meta name="author" content="Damien Metzler">
        <meta name="gwt:property" content="locale=fr">
        <!-- Date: 2011-07-15 -->
        <base href="${Context.basePath}/portail" />
        </@block>

        <title>
            <@block name="title">
            Labs
            </@block>
        </title>

        <@block name="scripts">
        <script type="text/javascript" src="${skinPath}/js/jquery/jquery-1.3.2.min.js"></script>
        <script type="text/javascript" src="${skinPath}/js/jquery/jquery.cookie.js"></script>
        </@block>

        <@block name="css">
        <link rel="icon" type="image/x-icon" href="/nuxeo/img/logo.jpeg" />
        <link rel="shortcut icon"  type="image/x-icon" href="/nuxeo/img/logo.jpeg"/>
        <link rel="stylesheet" type="text/css" media="all" href="/nuxeo/site/portalCommon/skin/css/theme/main.css"/>
        <link rel="search" type="application/opensearchdescription+xml" title="Intralm" href="/nuxeo/site/skin/Intralm/searchIntralm.xml">
        </@block>

    </head>
    <body>
        <div id="FKtopContent">

            <@block name="topHeader">
            <div id="FKtopHeader">
                <!-- In this header, all div are float left except div id="_rightHeader" -->

                <div id="FKbacktointralm" >
                    <a href="${Context.basePath}/portail">${Context.getMessage('label.backToHome')}</a>
                </div>
                <div class="FKbgBlackSep">&nbsp;</div>
                <div id="FKcurrentDate">${This.getCurrentDate()}</div>
                <div id="FKrightHeader">
                    <div class="FKbgBlackSep">&nbsp;</div>
                    <div id="FKHelp">
                        <#include "common/help.ftl" />
                    </div>
                    <div id="FKidentity">
                        <#include "common/login.ftl" />
                    </div>
                </div>
            </div>
            </@block>

            <@block name="banner">
            <div id="FKbanner">
                <form id="FKsearchBox">
                    <span id="FKqimg">&nbsp;</span>
                    <input id="FKq" name="q">&nbsp;<button id="FKsearch" value="${Context.getMessage('label.search')}"/>${Context.getMessage('label.search')}</button>
                </form>
            </div>
            </@block>

            <div id="FKmaincontent">
                <@block name="tabs">
                  <li class="FKselected home">&nbsp;Accueil</li>
              <li class="team">&nbsp;Equipe</li>
              <li class="team">&nbsp;Services internes</li>
                  <li class="myspace">&nbsp;My Custom Administrator</li>
                </@block>

                <@block name="manager">

                <div id="FKmanagerTabPanel">
                    <ul id="FKmanagerTabs">
                        <li class="selected">Widget</li>
                        <li>Thèmes</li>
                        <li>Organisation</li>
                        <li>Administrer</li>
                    </ul>
                    <div style="clear:both;"></div>
                    <div class="FKmanagerTab" id="addWidget">
                        <ul id="widgetCategories">
                            <li class="selected">Utilitaires</li>
                            <li>Collaboratifs</li>
                            <li>Médias</li>
                            <li>Commerce</li>
                        </ul>
                        <ul id="widgetList">
                            <li>
                                <img src="http://localhost/~dmetzler/lm-portal-offline/images/rss_icon.png"/>
                                <div>Flux RSS</div>
                                <button class="green">Ajouter</button>
                            </li>
                            <li>
                                <img src="http://localhost/~dmetzler/lm-portal-offline/images/rss_icon.png"/>
                                <div>Flux RSS</div>
                                <button class="green">Ajouter</button>
                            </li>
                        </ul>

                        <div style="clear:both;"></div>
                    </div>

                </div>
            </div>
            </@block>


            <@block name="content"></@block>

        </div>
        <div id="FKfooter">

            <div id="FKfooter_logo_lm">&nbsp;</div>
            <@block name="footer">
            <a href="/nuxeo/pdf/charte.pdf" target="_blank">${Context.getMessage('label.information')}</a> | ${Context.getMessage('label.contact')} : <a href="mailto:communicationinterne@leroymerlin.fr">communicationinterne@leroymerlin.fr</a>
            </@block>

            <div style="clear:both;"></div>

            <div id="FKfooter_bottom">
                <div id="FKfooter_logo_adeo"/>
            </div>
        </div>

    </body>
</html>