/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */
package bisq2.net

data class Address(val host: String, val port: Int) {

    companion object {
        fun from(fullyQualified: String) {
            val tokens = fullyQualified.split(":")
            Address(tokens[0], tokens[1].toInt())
        }

        fun localhost(port: Int) = Address("127.0.0.1", port)
    }

    override fun toString(): String = host + ":" + port
}