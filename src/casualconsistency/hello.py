
import sys

# print ('Number of arguments:', len(sys.argv), 'arguments.')
print ('Argument List:', str(sys.argv))
# takes in a variable list l which will have the following format:
# ['python.py, 'timestamp', 'message1', 'timestamp', 'message2']

def message(l):
    # we need a copy because we are going to take out values 
    copy = l
    q = []

    # Append all the timestamps, sort them in that list
    timestamp = []
    for i in range(1,len(l),2)
        timpstamp.append(l[i])

    # might actually have to manipulate the timestamp to get its actual time
    timestamp.sort()

    # find the timestamp in the l list 
    for time in timestamp:
        if (time in copy):
            # this index is just of the timestamp 
            index = copy.index(time)
            # we are going to append the message to the q
            q.append(copy[index+1])
            # remove the timestamp and the message from copy avoid duplicate timestamps 
            copy.pop(index)
            copy.pop(index+1)
     
    #  put messages in a queue to print so that we can delay the prints
    # for i in queue:
    # maybe sleep?
    # print(i)
    
    # unsure what to return yet
