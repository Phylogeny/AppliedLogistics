/*
 *
 * LIMITED USE SOFTWARE LICENSE AGREEMENT
 * This Limited Use Software License Agreement (the "Agreement") is a legal agreement between you, the end-user, and the FlatstoneTech Team ("FlatstoneTech"). By downloading or purchasing the software material, which includes source code (the "Source Code"), artwork data, music and software tools (collectively, the "Software"), you are agreeing to be bound by the terms of this Agreement. If you do not agree to the terms of this Agreement, promptly destroy the Software you may have downloaded or copied.
 * FlatstoneTech SOFTWARE LICENSE
 * 1. Grant of License. FlatstoneTech grants to you the right to use the Software. You have no ownership or proprietary rights in or to the Software, or the Trademark. For purposes of this section, "use" means loading the Software into RAM, as well as installation on a hard disk or other storage device. The Software, together with any archive copy thereof, shall be destroyed when no longer used in accordance with this Agreement, or when the right to use the Software is terminated. You agree that the Software will not be shipped, transferred or exported into any country in violation of the U.S. Export Administration Act (or any other law governing such matters) and that you will not utilize, in any other manner, the Software in violation of any applicable law.
 * 2. Permitted Uses. For educational purposes only, you, the end-user, may use portions of the Source Code, such as particular routines, to develop your own software, but may not duplicate the Source Code, except as noted in paragraph 4. The limited right referenced in the preceding sentence is hereinafter referred to as "Educational Use." By so exercising the Educational Use right you shall not obtain any ownership, copyright, proprietary or other interest in or to the Source Code, or any portion of the Source Code. You may dispose of your own software in your sole discretion. With the exception of the Educational Use right, you may not otherwise use the Software, or an portion of the Software, which includes the Source Code, for commercial gain.
 * 3. Prohibited Uses: Under no circumstances shall you, the end-user, be permitted, allowed or authorized to commercially exploit the Software. Neither you nor anyone at your direction shall do any of the following acts with regard to the Software, or any portion thereof:
 * Rent;
 * Sell;
 * Lease;
 * Offer on a pay-per-play basis;
 * Distribute for money or any other consideration; or
 * In any other manner and through any medium whatsoever commercially exploit or use for any commercial purpose.
 * Notwithstanding the foregoing prohibitions, you may commercially exploit the software you develop by exercising the Educational Use right, referenced in paragraph 2. hereinabove.
 * 4. Copyright. The Software and all copyrights related thereto (including all characters and other images generated by the Software or depicted in the Software) are owned by FlatstoneTech and is protected by United States copyright laws and international treaty provisions. FlatstoneTech shall retain exclusive ownership and copyright in and to the Software and all portions of the Software and you shall have no ownership or other proprietary interest in such materials. You must treat the Software like any other copyrighted material. You may not otherwise reproduce, copy or disclose to others, in whole or in any part, the Software. You may not copy the written materials accompanying the Software. You agree to use your best efforts to see that any user of the Software licensed hereunder complies with this Agreement.
 * 5. NO WARRANTIES. FLATSTONETECH DISCLAIMS ALL WARRANTIES, BOTH EXPRESS IMPLIED, INCLUDING BUT NOT LIMITED TO, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE WITH RESPECT TO THE SOFTWARE. THIS LIMITED WARRANTY GIVES YOU SPECIFIC LEGAL RIGHTS. YOU MAY HAVE OTHER RIGHTS WHICH VARY FROM JURISDICTION TO JURISDICTION. FlatstoneTech DOES NOT WARRANT THAT THE OPERATION OF THE SOFTWARE WILL BE UNINTERRUPTED, ERROR FREE OR MEET YOUR SPECIFIC REQUIREMENTS. THE WARRANTY SET FORTH ABOVE IS IN LIEU OF ALL OTHER EXPRESS WARRANTIES WHETHER ORAL OR WRITTEN. THE AGENTS, EMPLOYEES, DISTRIBUTORS, AND DEALERS OF FlatstoneTech ARE NOT AUTHORIZED TO MAKE MODIFICATIONS TO THIS WARRANTY, OR ADDITIONAL WARRANTIES ON BEHALF OF FlatstoneTech.
 * Exclusive Remedies. The Software is being offered to you free of any charge. You agree that you have no remedy against FlatstoneTech, its affiliates, contractors, suppliers, and agents for loss or damage caused by any defect or failure in the Software regardless of the form of action, whether in contract, tort, includinegligence, strict liability or otherwise, with regard to the Software. Copyright and other proprietary matters will be governed by United States laws and international treaties. IN ANY CASE, FlatstoneTech SHALL NOT BE LIABLE FOR LOSS OF DATA, LOSS OF PROFITS, LOST SAVINGS, SPECIAL, INCIDENTAL, CONSEQUENTIAL, INDIRECT OR OTHER SIMILAR DAMAGES ARISING FROM BREACH OF WARRANTY, BREACH OF CONTRACT, NEGLIGENCE, OR OTHER LEGAL THEORY EVEN IF FLATSTONETECH OR ITS AGENT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR FOR ANY CLAIM BY ANY OTHER PARTY. Some jurisdictions do not allow the exclusion or limitation of incidental or consequential damages, so the above limitation or exclusion may not apply to you.
 */

package tech.flatstone.appliedlogistics.common.grid;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import tech.flatstone.appliedlogistics.api.features.ITransport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class transportGrid implements ITransport {
    private DirectedAcyclicGraph<UUID, FilteredEdge> graph;
    private Map<UUID, UUID> exitNodeMap;

    public transportGrid() {
        graph = new DirectedAcyclicGraph<UUID, FilteredEdge>(
                new ClassBasedEdgeFactory<UUID, FilteredEdge>(FilteredEdge.class)
        );
        exitNodeMap = new HashMap<UUID, UUID>();
    }

    /**
     * Creates routing node
     * no limit on how may nodes this node can connect to
     *
     * @return
     */
    @Override
    public UUID createTransportNode() {
        UUID uuid = UUID.randomUUID();
        graph.addVertex(uuid);
        return uuid;
    }

    /**
     * Creates a node that accepts input into the routing network
     * can only connect to one other node
     *
     * @param parentNode
     * @return
     */
    @Override
    public UUID createEntryNode(UUID parentNode) {
        UUID uuid = UUID.randomUUID();
        graph.addVertex(uuid);
        graph.addEdge(uuid, parentNode);
        return uuid;
    }

    /**
     * Creates a node that receives routed objects from the network
     * can only connect to one other node
     *
     * @param parentNode
     * @return
     */
    @Override
    public UUID createExitNode(UUID parentNode) {
        UUID uuid = UUID.randomUUID();
        graph.addVertex(uuid);
        graph.addEdge(parentNode, uuid);
        exitNodeMap.put(uuid, parentNode);
        return uuid;
    }

    /**
     * Connects two nodes allowing objects to flow in one direction
     *
     * @param startNode
     * @param destNode
     * @return
     */
    @Override
    public boolean createDirectionalNodeConnection(UUID startNode, UUID destNode) {
        if ((exitNodeMap.containsKey(destNode)) || (exitNodeMap.containsKey(startNode))) {
            return false;
        }
        graph.addEdge(startNode, destNode);
        return true;
    }

    /**
     * Connects two nodes allowing objects to flow in both directions
     *
     * @param node1
     * @param node2
     * @return
     */
    @Override
    public boolean createNodeConnection(UUID node1, UUID node2) {
        if ((exitNodeMap.containsKey(node1)) || (exitNodeMap.containsKey(node2))) {
            return false;
        }
        graph.addEdge(node1, node2);
        graph.addEdge(node2, node1);
        return true;
    }

    /**
     * List of objects that the exit node will accept
     * overwrites an existing whitelist or blacklist
     * empty whitelist will cause node to accept no objects
     * Strings in list can be regular expression
     *
     * @param exitNode
     * @param unlocalizedNameList
     * @return
     */
    @Override
    public boolean applyWhitelistToNode(UUID exitNode, ArrayList<String> unlocalizedNameList) {
        UUID parentNode = exitNodeMap.get(exitNode);
        graph.getEdge(parentNode, exitNode).setWhitelist(unlocalizedNameList);
        return true;
    }

    /**
     * List of objects that the exit node will reject
     * overwrites an existing whitelist or blacklist
     * empty blacklist will cause node to accept all objects
     * Strings in list can be regular expression
     *
     * @param exitNode
     * @param unlocalizedNameList
     * @return
     */
    @Override
    public boolean applyBlacklistToNode(UUID exitNode, ArrayList<String> unlocalizedNameList) {
        UUID parentNode = exitNodeMap.get(exitNode);
        graph.getEdge(parentNode, exitNode).setBlacklist(unlocalizedNameList);
        return true;
    }

}
