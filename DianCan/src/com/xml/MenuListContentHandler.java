package com.xml;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
public class MenuListContentHandler extends DefaultHandler {
//	private List<ChiHuoMenuInfo> infos = null;
//	private ChiHuoMenuInfo menuInfo = null;
//	private String tagName = null;
//
//	public MenuListContentHandler(List<ChiHuoMenuInfo> infos) {
//		super();
//		this.infos = infos;
//	}
//
//	public List<ChiHuoMenuInfo> getInfos() {
//		return infos;
//	}
//
//	public void setInfos(List<ChiHuoMenuInfo> infos) {
//		this.infos = infos;
//	}
//
//	@Override
//	public void characters(char[] ch, int start, int length)
//			throws SAXException {
//		String temp = new String(ch, start, length);
//		if (tagName.equals("id")) {
//			menuInfo.setId(temp);
//		} else if (tagName.equals("menu.name")) {
//			menuInfo.setName(temp);
//		} else if (tagName.equals("menu.mark")) {
//			menuInfo.setMark(temp);
//		} else if (tagName.equals("menu.src")) {
//			menuInfo.setSrc(temp);
//		}
//	}
//
//	@Override
//	public void endDocument() throws SAXException {
//	}
//
//	@Override
//	public void endElement(String uri, String localName, String qName)
//			throws SAXException {
//		if (qName.equals("resource")) {
//			infos.add(menuInfo);
//		}
//		tagName = "";
//
//	}
//
//	@Override
//	public void startDocument() throws SAXException {
//		// TODO Auto-generated method stub
//		super.startDocument();
//	}
//
//	@Override
//	public void startElement(String uri, String localName, String qName,
//			Attributes attributes) throws SAXException {
//		this.tagName = localName;
//		if (tagName.equals("resource")) {
//			menuInfo = new ChiHuoMenuInfo();
//		}
//	}

}
