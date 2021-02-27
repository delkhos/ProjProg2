package rogue

import java.awt.{Color,Graphics2D, Graphics}

class Monster(arg_pos: Position, sprite: Sprite, collidable: Boolean, maxHealth: Int, ia: ArtificialIntelligence, hitChance: Int, hitDamage: Int,name_arg: String,name_color: String) extends LivingEntity(arg_pos,sprite, collidable, maxHealth, hitChance, hitDamage, name_arg, name_color) {
  val own_ia = ia
  def processDecision(game: GameObject){
    own_ia.processDecision(game,this)
  }
}


class Goblin(arg_pos: Position) extends Monster(arg_pos,
    new Sprite( Array[SubSprite](new SubSprite('g'.toInt,"000255000")), new Color((1.0).toFloat,(1.0).toFloat,(1.0).toFloat,(0.0).toFloat) )
    ,
    true,
    20,
    new IdleChaseIA
    ,40
    ,3
    ,"Goblin"
    ,"000255000"){
}

// la génération de biome par pouring et une meilleur gestion des biomes aléatoire, et plus lisible serait mieux ( en fait le solide rayé rouge est peut être un peu too much ), plutôt utiliser le point simple, le deux, le double tilde ....). Quand on génère une salle : générer un entier aléatorie entre 3 et 6 (par exemple) qui sera le nombre de biome différents. Pour un biome on fait un pouring qui set le biome dans un nouveau array[array] (comme floor). Nouvelle idée pour le biome: Un biome a une taille minimale et une taille maximale (qaund on génère le biome on choisit aléatoirement la taille entre ces deux bornes). Ensuite une biome est une liste de paire (pourcentage, environnement) classé par ordre croissant de pourcentage. Une fois les biome set, on parcours la carte, et pour chaque case on lance un nombre aléatoire, si il valide le premier élément de la liste, on set le nouveau env, si ce n'est pas le cas, on passe au suivant etc... typiquement le dernier élément de la liste d'un biome devrait alors 100% de chance d'avoir lieu.

// pour les monstres ajouter une fonction spawn
//
// L'inventaire a été ajouté mais il faudrait ajouter une fonctionnalité pour pouvoir "drop" un item en utilisant le clic droit
// Ajouter une fonctionnalité "observe" avec la souris : quand on survole une case de la game, (utiliser clicked_x (u devrais voir dans la mainloop de quoi il s'agit), ou quand on survole une case de l'inventaire (utiliser ui_clicked_x , idem), afficher en bas à droite une description de ce que l'on survole. Il faudrait donc ajouter un champs "description" aux entités. (devrait ont le faire aussi pour environnement ?
// ajouter des monstres. Quelques idées, des variations du gobelins juste avec un skin et des stats différentes. Faire de nouvelles IA, genre une IA chasseuse, qui sait toujours où est le héros et est toujours en chasing. Idée de mob : le hunter, quand il spawn affichait dans le log "The hero heard a hunter howl", pour que le joueur sache qu'il est traqué. D'où la fonction spawn qui permet de faire des choses particulières qaund on spawn un monstre.
// Ajouter une fonctions qui génère les mobs et objets de l'étage aléatoirement.
// Si le temps ajouter un objet de victoire. Si on le ramasse la partie est gagné (histoire d'avoir un moyen de victoire temporaire (peut être faudrait il en ramasser 4 pour plus de challenge). Et ajouter le fait que l'on perde si le héros meurt. Si temps et courage, ajouter plusieurs étages, mais pkutôt à faire pendant la seconde partie du projet.
