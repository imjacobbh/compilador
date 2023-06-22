"""
Swing JTree example in Jyhton.

Sticking with my City theme, this createa a J tree with 2 branches
one with cities starting with M and the other branch with cties starting
with S. This examples using a button event to show the selected city.

Greg Moore
Sept 2007
"""

from javax.swing import *
from java.awt import *
from javax.swing.tree import DefaultMutableTreeNode

class Example:

  def __init__(self):
    mCitiesData = ['Memphis', 'Melbourne', 'Milan',
                   'Marrakech', 'Moscow', 'Munich']

    sCitiesData = ['San Francisco', 'Salzburg', 'Santiago',
                   'Sydney', 'Sandnessjoen', 'Stockholm']

    frame = JFrame("Jython JTree Example")
    frame.setSize(400, 350)
    frame.setLayout(BorderLayout())

    root = DefaultMutableTreeNode('Cities')
    mCities = DefaultMutableTreeNode('Cities starting with M')
    sCities = DefaultMutableTreeNode('Cities starting with S')
    root.add(mCities)
    root.add(sCities)

    #now add the cities starting with M & S
    self.addCities(mCities, mCitiesData)
    self.addCities(sCities, sCitiesData)
    self.tree = JTree(root)

    scrollPane = JScrollPane()  # add a scrollbar to the viewport
    scrollPane.setPreferredSize(Dimension(300,250))
    scrollPane.getViewport().setView((self.tree))

    panel = JPanel()
    panel.add(scrollPane)
    frame.add(panel, BorderLayout.CENTER)

    btn = JButton('Select', actionPerformed = self.citySelect)
    frame.add(btn,BorderLayout.SOUTH)
    self.label = JLabel('Select city')
    frame.add(self.label, BorderLayout.NORTH)
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.setVisible(True)
    self.addCities

  def addCities(self, branch, branchData=None):
    '''  add data to tree branch
         requires branch and data to add to branch
    '''
    # this does not check to see if its a valid branch
    if branchData == None:
        branch.add(DefaultMutableTreeNode('No valid data'))
    else:
        for item in branchData:
          # add the data from the specified list to the branch
          branch.add(DefaultMutableTreeNode(item))

  def citySelect(self, event):
    selected = self.tree.getLastSelectedPathComponent()
    #check to make sure a city is selected
    if selected == None:
      self.label.text = 'No city selected'
    else:
      self.label.text = str(selected)
    #this is more Jythonic then:
    #self.label.text = selected.toString()

if __name__ == '__main__':
 Example()