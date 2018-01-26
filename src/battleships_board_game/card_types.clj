(ns battleships-board-game.card-types)

(defmacro def-map-tiles [name & body]
  (let [tiles (->> body
                   (mapv str)
                   (partition 4)
                   (partition 4))]
  `(def ~name '~tiles)))

(defn make-ship [[name health cbs desc]]
  {:name name
   :health health
   :cbs cbs
   :description desc})

(defn make-item [[name desc]]
  {:name name
   :description desc})

(defmacro def-ship-cards [name & body]
  (let [ships (->> body
                   (partition 4)
                   (map make-ship))]
  `(def ~name '~ships)))

(defmacro def-item-cards [name & body]
  (let [ships (->> body
                   (partition 2)
                   (map make-item))]
  `(def ~name '~ships)))
