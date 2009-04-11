#!/usr/bin/env python
# -*- coding: utf-8 -*-
import cgi
import wsgiref.handlers
import os
import xml.parsers.expat
import urllib
import re
import urllib2
from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext import db
from google.appengine.ext.webapp import template
from google.appengine.api import urlfetch

class YahooJLP:    
    
    def parse(self,str):
        key = 'FGoDDduxg64oBMu1pSndqhogq1KN5ASODXX20.pcreQDnGJAhTU7QFw8p.8-'
        self.restrant = []
        encoding = 'utf-8'
        url = 'http://jlp.yahooapis.jp/MAService/V1/parse?appid='
        url += key
        url += '&' + urllib.urlencode({"sentence":str})
        result = urlfetch.fetch(url)
        if result.status_code == 200:
            return self.parseYahooXML(result.content)
        else:
            return nil;
        
    #食べログAPI結果SAXパース用ロジック
    words = []
    item = {}
    itemName = ''
    itemVal = ''
    def start_element(self, name, attrs):
        if (name == "word"):
            self.item = {} 
            self.itemName = ""
        elif (name == "surface"):
            self.itemName = "surface"
        elif (name == "reading"):
            self.itemName = "reading"
        elif (name == "pos"):
            self.itemName = "pos"
        elif (name == "baseform"):
            self.itemName = "baseform"
        else:
            self.itemName = ''
        self.itemValue = ''
        
    def end_element(self, name):
        if (name == "word"):
            self.words.append(self.item)
            self.item = {} 
            self.itemName = ""
        elif (name == "surface"):
            self.item[self.itemName] = self.itemVal
        elif (name == "reading"):
            self.item[self.itemName] = self.itemVal
        elif (name == "pos"):
            self.item[self.itemName] = self.itemVal
        elif (name == "baseform"):
            self.item[self.itemName] = self.itemVal
        else:
            self.itemName = ''
            self.itemVal = ''

    def character_data(self, data):
        if (self.itemName == "surface"):
            self.itemVal = data
        elif (self.itemName == "reading"):
            self.itemVal = data
        elif (self.itemName == "pos"):
            self.itemVal = data
        elif (self.itemName == "baseform"):
            self.itemVal = data

    def parseYahooXML(self, content):
        p = xml.parsers.expat.ParserCreate()
        p.CharacterDataHandler = self.character_data
        p.StartElementHandler = self.start_element
        p.EndElementHandler = self.end_element
        p.Parse(content)
        return self.words

    
    
    
    
