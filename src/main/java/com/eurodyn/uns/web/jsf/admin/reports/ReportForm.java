package com.eurodyn.uns.web.jsf.admin.reports;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.taglibs.standard.tag.common.core.SetSupport;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.NotificationFacade;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.web.jsf.BaseBean;
import com.eurodyn.uns.web.jsf.SortableTable;

public class ReportForm extends BaseBean {

	protected UserFacade userFacade;

	protected ChannelFacade channelFacade;

	protected NotificationFacade notificationFacade;

	protected Date fromDate;

	protected Date toDate;

	protected Channel channel;

	protected User user;
	
	protected Subscription subscription;

	protected List channels;

	protected List users;

	protected List throuthputRecords;

	protected List failedNotificationsRecords;

	protected int totalSuccess = 0;

	protected int totalFailed = 0;
	
	protected SortableTable st1;
	
	public SortableTable getSt1() {
		return st1;
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public String getFormatedFromDate() {
		return sdf.format(fromDate);
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public String getFormatedToDate() {
		return sdf.format(toDate);
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public User getUser() {
		return user;
	}

	public List getChannels() {
		return channels;
	}

	public void setChannels(List channels) {
		this.channels = channels;
	}

	public List getUsers() {
		return users;
	}

	public void setUsers(List users) {
		this.users = users;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List getChannelsItems() {
		return toSelectItems(channels, "id", "title");
	}

	public List getUsersItems() {
		return toSelectItems(users, "id", "externalId");
	}

	public List getFailedNotificationsRecords() {
		return failedNotificationsRecords;
	}
	
	public void setFailedNotificationsRecords(List failedNotificationsRecords) {
		this.failedNotificationsRecords = failedNotificationsRecords;
	}

	public List getThrouthputRecords() {
		return st.sort(throuthputRecords);
	}

	public void setThrouthputRecords(List throuthputRecords) {
		this.throuthputRecords = throuthputRecords;
	}

	public int getTotalFailed() {
		return totalFailed;
	}

	public int getTotalSuccess() {
		return totalSuccess;
	}

	public void setTotalFailed(int totalFailed) {
		this.totalFailed = totalFailed;
	}

	public void setTotalSuccess(int totalSuccess) {
		this.totalSuccess = totalSuccess;
	}

	public int getTotal() {
		return totalSuccess + totalFailed;
	}

	public Subscription getSubscription() {
		return subscription;
	}
	
	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}
	
}