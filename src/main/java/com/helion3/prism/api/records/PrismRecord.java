/**
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.helion3.prism.api.records;

import org.spongepowered.api.block.BlockLoc;
import org.spongepowered.api.entity.player.Player;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.helion3.prism.queues.RecordingQueue;
import com.helion3.prism.records.BlockEventRecord;

/**
 * An easy-to-understand factory class for Prism {@link EventRecord}s.
 * 
 * By chaining methods together, you can build a record with
 * natural-language style syntax.
 * 
 * For example:
 * 
 * new PrismRecord().player(player).brokeBlock(blockLoc).save()
 * 
 */
public class PrismRecord {
    
    private String eventName;
    private EventSource source;
    private Optional<BlockLoc> optionalExistingBlock = Optional.absent();
    private Optional<BlockLoc> optionalReplacementBlock = Optional.absent();
    
    /**
     * Describe the Player responsible for the event this
     * record describes.
     * 
     * @param player Player responsible for this event
     * @return RecordBuilder
     */
    public PrismRecord player(Player player){
        this.source = new EventSource(player);
        return this;
    }

    /**
     * Describes a single block break. The location is automatically
     * obtained from the BlockLoc.
     * 
     * @param block Block broken.
     * @return RecordBuilder
     */
    public PrismRecord brokeBlock(BlockLoc block){
        checkNotNull(block);
        this.eventName = "block-break";
        this.optionalExistingBlock = Optional.of(block);
        return this;
    }
    
    /**
     * Build the final event record and send it to the queue.
     */
    public void save(){
        
        EventRecord record = null;

        // Block Break (only existing block present)
        if (optionalExistingBlock.isPresent() && !optionalReplacementBlock.isPresent()) {
            BlockLoc existingBlock = optionalExistingBlock.get();
            record = new BlockEventRecord(eventName, source, existingBlock.getLocation(), existingBlock.getType().getId());
        }
        
        // Queue the finished record for saving
        if (record != null) {
            RecordingQueue.add(record);
        }
    }
}