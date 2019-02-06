package com.khemarachuon.openshift.audit;

import java.util.LinkedList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserIdChain {
	private LinkedList<String> userIds;

	/**
	 * @return the userIds
	 */
	@XmlElement(name="userId")
	public LinkedList<String> getUserIds() {
		return userIds;
	}

	/**
	 * @param userIds the userIds to set
	 */
	public void setUserIds(LinkedList<String> userIds) {
		this.userIds = userIds;
	}
}
