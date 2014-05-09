/* ********************************************************************
    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.
*/
package org.bedework.notifier.cnctrs;

import org.bedework.notifier.exception.NoteException;
import org.bedework.notifier.notifications.Note;
import org.bedework.util.misc.ToString;

import org.apache.http.HttpStatus;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.DeleteItemResponseType;

import java.util.ArrayList;
import java.util.List;

/** The interface implemented by connectors. A connector instance is obtained
 * from a connector to handle a specific end of a specific subscription - items of
 * inforamtion handed to the getConnectorInstance method.
 *
 * @author Mike Douglass
 */
public interface ConnectorInstance {
  /** Called when a subscription is activated on synch engine startup or after
   * creation of a new subscription.
   *
   * @return status + messages
   * @throws org.bedework.notifier.exception.NoteException
   */
  BaseResponseType open() throws NoteException;

  /**
   * @return the connector for this instance
   * @throws org.bedework.notifier.exception.NoteException
   */
  Connector getConnector() throws NoteException;

  /** Called before a resynch takes place to determine if the end point has
   * changed and needs resynch. Only the source end of a subscription will be
   * checked. Note that false positives may occur if changes happen outside of
   * the synch time boundaries. For notification driven endpoints this can
   * probably always return false.
   *
   * @return true if a change occurred
   * @throws org.bedework.notifier.exception.NoteException
   */
  boolean changed() throws NoteException;


  /** Information used to list notifications
   * This information is only valid in the context of a given subscription.
   */
  public static class ItemInfo {
    /** */
    public String href;

    /** */
    public String lastMod;

    /** */
    public boolean seen;

    /** Status of last operation */
    public HttpStatus status;

    /**
     * @param href for the resource
     * @param lastMod last time updated
     */
    public ItemInfo(final String href,
                     final String lastMod) {
      this.href = href;
      this.lastMod = lastMod;
    }

    @Override
    public String toString() {
      final ToString ts = new ToString(this);

      ts.append("href", href);
      ts.append("lastMod", lastMod);

      return ts.toString();
    }
  }

  /**
   */
  public class NotifyItemsInfo extends BaseResponseType {
    /** the items.
     */
    public List<ItemInfo> items = new ArrayList<>();
  }

  /** Get all notifications..
   *
   * @return List of items - never null, maybe empty.
   * @throws NoteException
   */
  NotifyItemsInfo getItemsInfo() throws NoteException;

  /** Delete a resource.
   *
   * @param item the resource
   * @return response
   * @throws NoteException
   */
  DeleteItemResponseType deleteItem(ItemInfo item) throws NoteException;

  /** Fetch a resource.
   *
   * @param item the resource
   * @return response
   * @throws NoteException
   */
  Note fetchItem(ItemInfo item) throws NoteException;

  /** Fetch a batch of resources. The number and order of the result
   * set must match that of the parameter uids.
   *
   * @param items the resources
   * @return responses
   * @throws NoteException
   */
  List<Note> fetchItems(List<ItemInfo> items) throws NoteException;

  /** Update a notification.
   *
   * @param note - specifyng the item to be updated
   * @return true OK -false check status
   * @throws NoteException
   */
  boolean updateItem(Note note) throws NoteException;
}
